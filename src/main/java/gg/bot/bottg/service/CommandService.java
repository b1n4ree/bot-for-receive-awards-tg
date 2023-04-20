package gg.bot.bottg.service;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.*;
import gg.bot.bottg.condition.Conditions;
import gg.bot.bottg.data.entity.User;
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

    @Autowired
    UserRepository userRepository;

    private final ConditionService conditionService;
    private final TelegramBot telegramBot;
    private final KeyboardService keyboardService;
    private final ConnectionGizmoService connectionGizmoService;

    public CommandService(ConditionService conditionService, TelegramBot telegramBot, KeyboardService keyboardService, ConnectionGizmoService connectionGizmoService) {
        this.conditionService = conditionService;
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

                conditionService.saveCondition(telegramUserId, Conditions.START);
                userRepository.save(userStart);

                log.info(ANSI_GREEN + "[/START]  User is saved by tgId=[" + telegramUserId  + "] and " +
                        "condition=[" + Conditions.START + "]");

                telegramBot.execute(new SendMessage(telegramUserId, "Добро пожаловать. Это бот"));
                telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));

                telegramBot.execute(new SendMessage(telegramUserId, "Бот предназначен для получения" +
                        " наград за покупки товаров/пакетов времени, внесение депозитов\uD83E\uDD73 \nКак же получить?" +
                        "\n - Приходишь в GG + \n - Делаешь покупку(и) на минимальную необходимую сумму" +
                        "\n - Заходишь бота. Вводишь ник, на который совершаешь покупки в клубе, и пароль. " +
                        "Данные аккаунта проверяются").replyMarkup(keyboardService.chooseYeaOrNo()));

            } else {

                User userStartExist = userRepository.getUserByTelegramId(telegramUserId).get();

                if (userStartExist.getAuthorizationInGizmoAccount()) {

                    conditionService.updateCondition(telegramUserId, Conditions.USER_IS_EXIST);
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
                || "Да ☺\uFE0F".equals(update.message().text())) {

            conditionService.updateCondition(telegramUserId, Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
            User user = userRepository.getUserByTelegramId(telegramUserId).get();
            user.setCondition(Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
            userRepository.save(user);

            telegramBot.execute(new SendMessage(telegramUserId, "Введите ник из клуба: "));

            log.info(ANSI_GREEN + "[/SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN]  User is saved by tgId=[" + telegramUserId  + "] and " +
                    "condition=[" + conditionService.getCondition(telegramUserId) + "]");

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


        log.info(ANSI_GREEN + "Condition in method [waitGizmoLoginCommand]: " + conditionService.getCondition(telegramUserId)
                + ", " + user.getCondition());


        String userId = String.format(userIdGizmo, URLEncoder.encode(String.valueOf(update.message().text()), StandardCharsets.UTF_8));
        JsonObject jsonObject = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), userId);

        System.out.println(jsonObject);
        if (jsonObject.get("result").equals(null)) {

            user.setGizmoId(jsonObject.get("result").getAsLong());

        } else {
            user.setGizmoId(null);
        }
        conditionService.updateCondition(telegramUserId, Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_PASS);
        user.setCondition(Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_PASS);
        user.setGizmoName(String.valueOf(update.message().text()));
        userRepository.save(user);
        telegramBot.execute(new SendMessage(telegramUserId, "Теперь пароль:"));

        log.info(ANSI_GREEN + "[/SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN]  User is saved by tgId=[" + telegramUserId  + "] and " +
                "condition=[" + conditionService.getCondition(telegramUserId) + "] and loginGizmo=[" + update.message().text() + "]");
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
            conditionService.updateCondition(telegramUserId, Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
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
                conditionService.updateCondition(telegramUserId, Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
                userRepository.save(user);

            } else {

                telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));
                telegramBot.execute(new SendMessage(telegramUserId, "Аккаунт успешно привязан"));
                telegramBot.execute(new SendMessage(telegramUserId, "Теперь тебе доступны призы").replyMarkup(
                        keyboardService.firstInlineKeyboardWithPrizes(telegramUserId)));
                user.setAuthorizationInGizmoAccount(true);
                user.setCondition(Conditions.CHOOSE_PRIZE);
                user.setGizmoId(userGizmoId);
                conditionService.updateCondition(telegramUserId, Conditions.CHOOSE_PRIZE);
                userRepository.save(user);
            }
        } else {

            telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));
            telegramBot.execute(new SendMessage(telegramUserId, "Неправильные логин или пароль. Попробуйте ещё раз. Введи логин: "));

            conditionService.updateCondition(telegramUserId, Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
            user.setCondition(Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
            userRepository.save(user);
        }
    }
}
