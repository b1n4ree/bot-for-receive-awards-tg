package gg.bot.bottg.service;

import com.google.gson.JsonObject;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.*;
import gg.bot.bottg.data.entity.Action;
import gg.bot.bottg.data.repository.ActionRepository;
import gg.bot.bottg.enums.Actions;
import gg.bot.bottg.enums.Conditions;
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
import java.util.Optional;


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

    @Autowired
    ActionRepository actionRepository;

    private final TelegramBot telegramBot;
    private final KeyboardService keyboardService;
    private final ConnectionGizmoService connectionGizmoService;
    private final SessionService sessionService;

    public CommandService(TelegramBot telegramBot, KeyboardService keyboardService, ConnectionGizmoService connectionGizmoService, SessionService sessionService) {
        this.telegramBot = telegramBot;
        this.keyboardService = keyboardService;
        this.connectionGizmoService = connectionGizmoService;
        this.sessionService = sessionService;
    }


    public void startCommand(Update update) {

        Action action = new Action();
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

                telegramBot.execute(new SendSticker(telegramUserId, "CAACAgIAAxkBAAEI8htkXN6mcPt1AAEmxg1IBINt6P6H1oEAAtcYAAJuJuFLBWMtwpjr_KsvBA"));
                telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));
                telegramBot.execute(new SendMessage(telegramUserId, """
                        Это [GGBot](https://vk.com/ggclub36) 😎
                        
                        Бот будет выдавать тебе награды при совершении ежедневных денежных операций в клубе через свой аккаунт: покупка товаров из бара, пакетов времени. 
       
                         Для того, чтобы получить награду, нужно:
                        1️⃣ Привязать клубный аккаунт к боту
                        2️⃣ Прийти в GG и совершить любую покупку на аккаунте на сумму 100 рублей или более
                        3️⃣ После покупки зайти к боту и нажать в меню наград(_будет выслано после привязки акка_) на кнопку с текущем днём
                        """).parseMode(ParseMode.Markdown).replyMarkup(keyboardService.chooseYeaOrNo()));

                telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));
                telegramBot.execute(new SendMessage(telegramUserId, "Для того, чтобы продолжить пользоваться ботом, нажми \"Да\"")
                        .replyMarkup(keyboardService.chooseYeaOrNo()));

                action.setAction(Actions.START_COMMAND_NEW_USER.toString());
                action.setCurrentStreakDay(userStart.getCurrentStreakDay());
                action.setUserTelegramId(telegramUserId);
                action.setUserTelegramNickname(update.message().from().username());
                actionRepository.save(action);

            } else {

                User userStartExist = userRepository.getUserByTelegramId(telegramUserId).get();

                if (userStartExist.getAuthorizationInGizmoAccount()) {

                    action.setAction(Actions.START_COMMAND_EXIST_USER.toString());
                    action.setCurrentStreakDay(userStartExist.getCurrentStreakDay());
                    action.setUserTelegramId(telegramUserId);
                    action.setUserTelegramNickname(update.message().from().username());
                    actionRepository.save(action);

                    userStartExist.setCondition(Conditions.USER_IS_EXIST);
                    userRepository.save(userStartExist);
                    telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));
                    telegramBot.execute(new SendMessage(telegramUserId, "Вы уже авторизованы. " +
                            "Сейчас отправлю меню с наградами").replyMarkup(new ReplyKeyboardRemove()));

                    telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));
                    telegramBot.execute(new SendMessage(telegramUserId, "Награды").replyMarkup(
                            keyboardService.firstInlineKeyboardWithPrizes(telegramUserId)));

                }
            }
        }
    }


    public void yesOrNoCommand(Update update) {

        Action action = new Action();
        Long telegramUserId = update.message().from().id();
        User user = userRepository.getUserByTelegramId(telegramUserId).get();

        if ("да".equalsIgnoreCase(update.message().text())
                || "Да ☺️".equals(update.message().text())) {

            user.setCondition(Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
            userRepository.save(user);

            telegramBot.execute(new SendMessage(telegramUserId, "Введите ник из клуба: "));

            action.setAction(Actions.YES_COMMAND.toString());
            action.setCurrentStreakDay(user.getCurrentStreakDay());
            action.setUserTelegramId(telegramUserId);
            action.setUserTelegramNickname(user.getTelegramNickname());
            actionRepository.save(action);

        } else if ("нет".equalsIgnoreCase(update.message().text())
                || "Нет \uD83E\uDD72".equals(update.message().text())) {

            action.setAction(Actions.NO_COMMAND.toString());
            action.setUserTelegramId(telegramUserId);
            action.setUserTelegramNickname(update.message().from().username());
            actionRepository.save(action);

            user.setCondition(Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
            userRepository.save(user);
            telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.choose_sticker));
            telegramBot.execute(new SendSticker(telegramUserId, "CAACAgIAAxkBAAEIeYVkLoJIo2uIHHsGtlVbvmad4Y-3dgACDgEAAsFGhhufbS2BFcch4i8E"));
            telegramBot.execute(new SendMessage(telegramUserId, "Эх, следи за новостями в группе vk.com/ggclub36"));
            telegramBot.execute(new SendMessage(telegramUserId, "Если захочешь продолжить пользоваться ботом, " +
                    "то просто начни вводить свой ник из клуба"));

        }
    }

    public void waitGizmoLoginCommand(Update update) {

        Action action = new Action();

        Long telegramUserId = update.message().from().id();

        User user = new User();
        if (userRepository.getUserByTelegramId(telegramUserId).isPresent()) {
            user = userRepository.getUserByTelegramId(telegramUserId).get();
        }

        String userId = String.format(userIdGizmo, URLEncoder.encode(String.valueOf(update.message().text()), StandardCharsets.UTF_8));
        JsonObject jsonObject = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), userId);

        System.out.println(jsonObject);
        if (jsonObject.get("result").equals(null)) {

            user.setGizmoId(jsonObject.get("result").getAsLong());

        } else {
            user.setGizmoId(null);
        }

        action.setUserTelegramId(telegramUserId);
        action.setUserTelegramNickname(update.message().from().username());
        action.setCurrentStreakDay(user.getCurrentStreakDay());
        action.setAction(Actions.GIZMO_PASS_ENTER.toString());
        actionRepository.save(action);

        user.setCondition(Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_PASS);
        user.setGizmoName(String.valueOf(update.message().text()));
        userRepository.save(user);

        telegramBot.execute(new SendMessage(telegramUserId, "Теперь пароль:"));
    }

    public void waitGizmoPasswordCommand(Update update) {

        Action action = new Action();
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

            action.setAction(Actions.GIZMO_LOGIN_AND_PASS_INCORRECT.toString());
            action.setCurrentStreakDay(user.getCurrentStreakDay());
            action.setUserTelegramId(telegramUserId);
            action.setUserTelegramNickname(user.getTelegramNickname());
            actionRepository.save(action);

            telegramBot.execute(new SendMessage(telegramUserId, "Неверные данные. Введите логин и пароль заново." +
                    "\nЛогин:"));
            userGizmoId = 11111111L;
            user.setCondition(Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
            userRepository.save(user);

            return;
        }

        Integer messageWithPasswordId = update.message().messageId();
        telegramBot.execute(new DeleteMessage(telegramUserId, messageWithPasswordId));

        if (validResult.equals(0L) && requiredInfoResult.equals(0L)) {

            List<User> users = userRepository.getUsersByGizmoName(user.getGizmoName()).get();

            if (users.size() > 1) {

                action.setAction(Actions.GIZMO_USER_EXIST.toString());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                action.setUserTelegramId(telegramUserId);
                actionRepository.save(action);

                telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));
                telegramBot.execute(new SendMessage(telegramUserId, "Этот аккаунт уже используется. Введите" +
                        " другие данные"));

                user.setCondition(Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
                userRepository.save(user);


            } else {

                action.setAction(Actions.GIZMO_LOGIN_AND_PASS_CORRECT.toString());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramId(telegramUserId);
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

                telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));
                telegramBot.execute(new SendMessage(telegramUserId, "Аккаунт успешно привязан").replyMarkup(new ReplyKeyboardRemove()));
                telegramBot.execute(new SendMessage(telegramUserId, "Теперь тебе доступен список наград" +
                        "\nПриходи в клуб каждый день, совершай покупки и получай награды!").replyMarkup(
                        keyboardService.firstInlineKeyboardWithPrizes(telegramUserId)));

                user.setAuthorizationInGizmoAccount(true);
                user.setCondition(Conditions.CHOOSE_PRIZE);
                user.setGizmoId(userGizmoId);
                userRepository.save(user);

            }
        } else {

            action.setAction(Actions.GIZMO_LOGIN_AND_PASS_INCORRECT.toString());
            action.setCurrentStreakDay(user.getCurrentStreakDay());
            action.setUserTelegramId(telegramUserId);
            action.setUserTelegramNickname(user.getTelegramNickname());

            telegramBot.execute(new SendChatAction(telegramUserId, ChatAction.typing));
            telegramBot.execute(new SendMessage(telegramUserId, "Неправильные логин или пароль. Попробуйте ещё раз. Введи логин: "));

            user.setCondition(Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
            userRepository.save(user);
        }


        }
    public void getPrizeInlineKeyboard(Update update) {

        Long telegramUserId = update.message().from().id();

        if (userRepository.getUserByTelegramId(telegramUserId).isPresent()) {

            User user = userRepository.getUserByTelegramId(telegramUserId).get();


            if ("/getawards".equalsIgnoreCase(update.message().text()) && user.getCondition() != null) {

                Action action = new Action();
                action.setAction(Actions.GETPRIZES_COMMAND.toString());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(update.message().from().username());
                action.setUserTelegramId(update.message().chat().id());
                actionRepository.save(action);

                if (user.getAuthorizationInGizmoAccount()) {
                    telegramBot.execute(new SendMessage(telegramUserId, "Награды").replyMarkup(
                            keyboardService.firstInlineKeyboardWithPrizes(telegramUserId)));
                } else {
                    telegramBot.execute(new SendMessage(telegramUserId, "Сначала нужно ввести данные аккаунта клуба"));
                }
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

    public void getComputersSessions(Update update) {

        Action action = new Action();
        Optional<User> userOptional = userRepository.getUserByTelegramId(update.message().chat().id());


        if ("/getcomps".equals(update.message().text())) {
            if (userOptional.isPresent()) {

                User user = userOptional.get();
                action.setAction(Actions.GET_COMPUTERS_SESSIONS_AUTH.toString());
                action.setUserTelegramId(user.getTelegramId());
                action.setUserTelegramNickname(user.getTelegramNickname());
                action.setCurrentStreakDay(user.getCurrentStreakDay());

                if (user.getAuthorizationInGizmoAccount()) {

                    sessionService.userActiveTime(update, true);

                } else {

                    telegramBot.execute(new SendMessage(user.getTelegramId(), "Комманда доступна только авторизованным" +
                            " пользователям"));
                }
            } else {
                action.setAction(Actions.GET_COMPUTERS_SESSIONS_NO_AUTH.toString());
            }
        }
        actionRepository.save(action);
    }

    public void deleteUserData(Update update) {

        Long telegramUserId = update.message().chat().id();
        Optional<User> user = userRepository.getUserByTelegramId(telegramUserId);

        if ("/delete".equals(update.message().text()) && user.isPresent() && !user.get().getCondition().equals(Conditions.DELETE_SELECT)) {

            telegramBot.execute(new SendMessage(telegramUserId, "Вы уверены, что хотите удалить свои данные? " +
                    "Стрик обнулится же:("));
            user.get().setCondition(Conditions.DELETE_SELECT);
            userRepository.save(user.get());
            telegramBot.execute(new SendMessage(telegramUserId, "Если до сих пор хотите удалить свои данные, " +
                    "то введите ещё раз команду для удаления"));

        } else if ("/delete".equals(update.message().text()) && user.isEmpty()) {

            telegramBot.execute(new SendMessage(telegramUserId, "Бот не хранит твои данные. " +
                    "Поэтому и нечего удалять"));

        } else if ("/delete".equals(update.message().text()) && user.get().getCondition().equals(Conditions.DELETE_SELECT)) {

            userRepository.delete(user.get());
            user.get().setIsDeleted(true);
            user.get().setGizmoName(null);
            user.get().setCondition(null);
            user.get().setIsZeroingStreak(true);
            telegramBot.execute(new SendMessage(telegramUserId, "Данные удалены"));
        }
    }

    public void sendMessageToUser(Update update) {

        if (update.message().text().startsWith("/sendmsg") && myTelegramId.equals(update.message().chat().id())) {

            String[] strArr = update.message().text().split(" ");
            Long id = Long.parseLong(strArr[1]);
            StringBuilder sb = new StringBuilder();

            for (int i = 2; i < strArr.length; i++) {
                sb.append(strArr[i]).append(" ");
            }

            telegramBot.execute(new SendMessage(id, sb.toString()));
        }
    }

    public void sendAllUsersIdAndName(Update update) {

        if ("/sendusers".equals(update.message().text()) && (myTelegramId.equals(update.message().chat().id()) ||
                update.message().chat().id().equals(792057L))) {

            List<User> userList = userRepository.findAll();
            StringBuilder sb = new StringBuilder();

            userList.forEach(user -> {
                sb.append(user.getTelegramFirstName()).append(" ").append(user.getTelegramNickname()).append(": ")
                        .append(user.getTelegramId()).append(" ").append(user.getCurrentStreakDay()).append("\n");
            });

            telegramBot.execute(new SendMessage(update.message().chat().id(), sb.toString()));
        }
    }

    public void sendMessageToAllUsers(Update update) {

        if (update.message().text().startsWith("/sendtoallmsg") && myTelegramId.equals(update.message().chat().id())) {

            List<User> users = userRepository.findAll();

            String[] strArr = update.message().text().split(" ");
            StringBuilder sb = new StringBuilder();

            for (int i = 1; i < strArr.length; i++) {
                sb.append(strArr[i]).append(" ");
            }

            if (!users.isEmpty()) {

                users.forEach(user -> {

                    telegramBot.execute(new SendMessage(user.getTelegramId(), sb.toString()).disableNotification(true));
                });
            }
        }
    }
}
