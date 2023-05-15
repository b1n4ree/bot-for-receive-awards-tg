package gg.bot.bottg.service;

import com.google.gson.JsonObject;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.*;
import gg.bot.bottg.condition.Conditions;
import gg.bot.bottg.data.entity.Prize;
import gg.bot.bottg.data.entity.User;
import gg.bot.bottg.data.repository.PrizeRepository;
import gg.bot.bottg.data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static gg.bot.bottg.service.BotService.ANSI_GREEN;


@Slf4j
@Component
public class CommandService {

    @Value("${gizmo_user_id}")
    private String userIdGizmo;

    @Value("${gizmo_user_password_valid}")
    private String gizmoUserValid;

    @Value("${telegram_id_al}")
    private Long myTelegramId;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PrizeRepository prizeRepository;

    private final TelegramBot telegramBot;
    private final KeyboardService keyboardService;
    private final ConnectionGizmoService connectionGizmoService;

    public CommandService(TelegramBot telegramBot, KeyboardService keyboardService, ConnectionGizmoService connectionGizmoService) {
        this.telegramBot = telegramBot;
        this.keyboardService = keyboardService;
        this.connectionGizmoService = connectionGizmoService;
    }


    public void startCommand(Update update) {

        Long telegramUserId = update.message().from().id();

        if ("/start".equalsIgnoreCase(update.message().text())) {

            if (userRepository.getUserByTelegramId(telegramUserId).isEmpty()) {

                User userStart = new User();
                userStart.setCondition(Conditions.START);
                userStart.setTelegramNickname(update.message().from().username());
                userStart.setTelegramFirstName(update.message().from().firstName());
                userStart.setTelegramSecondName(update.message().from().lastName());
                userStart.setTelegramId(update.message().from().id());

                userRepository.save(userStart);

                log.info(ANSI_GREEN + "[/START]  User is saved by tgId=[" + telegramUserId  + "] and " +
                        "condition=[" + Conditions.START + "]");

                telegramBot.execute(new SendSticker(telegramUserId, "CAACAgIAAxkBAAEI8htkXN6mcPt1AAEmxg1IBINt6P6H1oEAAtcYAAJuJuFLBWMtwpjr_KsvBA"));
                telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));


                telegramBot.execute(new SendMessage(telegramUserId, """
                        [inline URL](http://www.example.com/)
                        Это GGBot😎
                        Бот будет выдавать тебе награду. Для того, чтобы получить приз, нужно:
                        1️⃣ Привязать клубный аккаунт к боту
                        2️⃣ Прийти в GG и совершить любую покупку на аккаунте на сумму 100 рублей или более
                        3️⃣ После покупки зайти в бота и нажать в меню призов(_будет выслано после привязки акка_) на кнопку с текущем днём
                        """).parseMode(ParseMode.Markdown).replyMarkup(keyboardService.chooseYeaOrNo()));

            } else {

                User userStartExist = userRepository.getUserByTelegramId(telegramUserId).get();

                if (userStartExist.getAuthorizationInGizmoAccount()) {

                    userStartExist.setCondition(Conditions.USER_IS_EXIST);
                    telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));
                    telegramBot.execute(new SendMessage(telegramUserId, "Вы уже авторизованы. " +
                            "Сейчас отправлю меню с наградами").replyMarkup(new ReplyKeyboardRemove()));

                    telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));
                    telegramBot.execute(new SendMessage(telegramUserId, "").replyMarkup(
                            keyboardService.firstInlineKeyboardWithPrizes(telegramUserId)));
                }
            }
        }
    }


    public void yesOrNoCommand(Update update) {

        Long telegramUserId = update.message().from().id();

        if ("да".equalsIgnoreCase(update.message().text())
                || "Да ☺️".equals(update.message().text())) {

            User user = userRepository.getUserByTelegramId(telegramUserId).get();
            user.setCondition(Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
            userRepository.save(user);

            telegramBot.execute(new SendMessage(telegramUserId, "Введите ник из клуба: "));

            log.info(ANSI_GREEN + "[/SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN]  User is saved by tgId=[" + telegramUserId  + "] and " +
                    "condition=[" + user.getCondition() + "]");

        } else if ("нет".equalsIgnoreCase(update.message().text())
                || "Нет \uD83E\uDD72".equals(update.message().text())) {

            telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.choose_sticker));
            telegramBot.execute(new SendSticker(telegramUserId, "CAACAgIAAxkBAAEIeYVkLoJIo2uIHHsGtlVbvmad4Y-3dgACDgEAAsFGhhufbS2BFcch4i8E"));
            telegramBot.execute(new SendMessage(telegramUserId, "Эх, следи за новостями в группе."));

        }
    }

    public void waitGizmoLoginCommand(Update update) {

        Long telegramUserId = update.message().from().id();

        User user = new User();
        if (userRepository.getUserByTelegramId(telegramUserId).isPresent()) {
            user = userRepository.getUserByTelegramId(telegramUserId).get();
        }


        log.info(ANSI_GREEN + "Condition in method [waitGizmoLoginCommand]: " + user.getCondition());


        String userId = String.format(userIdGizmo, URLEncoder.encode(String.valueOf(update.message().text()), StandardCharsets.UTF_8));
        JsonObject jsonObject = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), userId);

        System.out.println(jsonObject);
        if (jsonObject.get("result").equals(null)) {

            user.setGizmoId(jsonObject.get("result").getAsLong());

        } else {
            user.setGizmoId(null);
        }
        user.setCondition(Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_PASS);
        user.setGizmoName(String.valueOf(update.message().text()));
        userRepository.save(user);
        telegramBot.execute(new SendMessage(telegramUserId, "Теперь пароль:"));

        log.info(ANSI_GREEN + "[/SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN]  User is saved by tgId=[" + telegramUserId  + "] and " +
                "condition=[" + user.getCondition() + "] and loginGizmo=[" + update.message().text() + "]");
    }

    public void waitGizmoPasswordCommand(Update update) {

        Long telegramUserId = update.message().from().id();
        User user = userRepository.getUserByTelegramId(telegramUserId).get();

        String userValid = String.format(gizmoUserValid, URLEncoder.encode(user.getGizmoName()),
                URLEncoder.encode(String.valueOf(update.message().text()), StandardCharsets.UTF_8));
        JsonObject jsonObject = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), userValid);

        System.out.println(user.getGizmoName() + " - " + jsonObject);

        Long validResult = jsonObject.getAsJsonObject("result").get("result").getAsLong();
        Long requiredInfoResult = jsonObject.getAsJsonObject("result").get("requiredInfo").getAsLong();
        Long userGizmoId;
        try {
            userGizmoId = jsonObject.getAsJsonObject("result").getAsJsonObject("identity")
                    .get("userId").getAsLong();
        } catch (UnsupportedOperationException e) {
            telegramBot.execute(new SendMessage(telegramUserId, "Неверные данные. Введите логин"));
            userGizmoId = 1111111L;
            user.setCondition(Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
            userRepository.save(user);
            return;
        }

        Integer messageWithPasswordId = update.message().messageId();
        telegramBot.execute(new DeleteMessage(telegramUserId, messageWithPasswordId));

        if (validResult.equals(0L) && requiredInfoResult.equals(0L)) {

            List<User> users = userRepository.getUsersByGizmoName(user.getGizmoName()).get();

            if (users.size() > 1) {

                telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));
                telegramBot.execute(new SendMessage(telegramUserId, "Этот аккаунт уже используется. Введите" +
                        " другие данные"));

                user.setCondition(Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
                userRepository.save(user);

            } else {

                telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));
                telegramBot.execute(new SendMessage(telegramUserId, "Аккаунт успешно привязан").replyMarkup(new ReplyKeyboardRemove()));
                telegramBot.execute(new SendMessage(telegramUserId, "Теперь тебе доступны призы").replyMarkup(
                        keyboardService.firstInlineKeyboardWithPrizes(telegramUserId)));
                user.setAuthorizationInGizmoAccount(true);
                user.setCondition(Conditions.CHOOSE_PRIZE);
                user.setGizmoId(userGizmoId);
                userRepository.save(user);
            }
        } else {

            telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));
            telegramBot.execute(new SendMessage(telegramUserId, "Неправильные логин или пароль. Попробуйте ещё раз. Введи логин: "));

            user.setCondition(Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
            userRepository.save(user);
        }


        }
    public void getPrizeInlineKeyboard(Update update) {

        Long telegramUserId = update.message().from().id();
        User user = userRepository.getUserByTelegramId(telegramUserId).get();

        if ("/getprizes".equalsIgnoreCase(update.message().text()) && user.getCondition() != null) {
            if (user.getAuthorizationInGizmoAccount()) {
                telegramBot.execute(new SendMessage(telegramUserId, "Призы").replyMarkup(
                        keyboardService.firstInlineKeyboardWithPrizes(telegramUserId)));
            } else {
                telegramBot.execute(new SendMessage(telegramUserId, "Сначала нужно ввести данные аккаунта клуба"));
            }
        }
    }

    public void changePrizeName(Update update) {

        Long telegramUserId = update.message().chat().id();

        if (myTelegramId.equals(telegramUserId)) {

            String[] strUpdate = update.message().text().split(", ");

            if (strUpdate[0].equalsIgnoreCase("setPrizes")) {
                Long id = Long.parseLong(strUpdate[1]);
                Prize prize = prizeRepository.findById(id).get();
                prize.setPrizeName(strUpdate[2]);
                prizeRepository.save(prize);
            }
        }
    }
}
