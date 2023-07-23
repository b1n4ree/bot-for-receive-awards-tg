package gg.bot.bottg.service;

import com.google.gson.JsonArray;
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
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Slf4j
@Component
public class CommandService {

    @Value("${gizmo_user_id}")
    private String userIdGizmo;

    @Value("${gizmo_user_password_valid}")
    private String gizmoUserValid;

    @Value("${telegram_id_al}")
    private Long myTelegramId;

    private List<String> phoneList;

    private final String urlToSendCallRecovery = "/api/v2/recovery/password/%s/phone";

    //Сначала токен, потом код, потом пароль
    private final String urlToConfirmAndEnterNewPass = "/api/v2.0/recovery/password/%s/%s/complete?newPassword=%s";
    private final String promo = "ILGG";

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
        phoneList = new ArrayList<>();
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

            telegramBot.execute(new SendMessage(telegramUserId, "Введите ник из клуба либо номер телефона, на который регистрировал аккаунт: ")
                    .replyMarkup(new ReplyKeyboardRemove()));
            telegramBot.execute(new SendMessage(user.getTelegramId(), "Для восстановления пароля используй комманду: /recpass"));

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
                    "то просто начни вводить свой ник из клуба")
                    .replyMarkup(new ReplyKeyboardRemove()));

        }
    }

    public void waitGizmoLoginCommand(Update update) {

        if ("/recpass".equals(update.message().text())) {

            recoveryPasswordCommandInfo(update);
            return;
        }

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

    public void recoveryPasswordCommandInfo(Update update) {

        Action action = new Action();
        Long telegramUserId = update.message().chat().id();
        User user = userRepository.getUserByTelegramId(telegramUserId).get();

        if ("/recpass".equals(update.message().text())) {

            user.setCondition(Conditions.RECPASS_ENTER_PHONE_NUMBER);
            userRepository.saveAndFlush(user);

            telegramBot.execute(new SendMessage(telegramUserId, "Теперь введи номер телефона, начиная с 7, без пробелов. " +
                    "Поступит звонок, нужно будет ввести последние 4 цифры номера для подтверждения. Если не поступает звонок или какие-то другие проблемы - свяжись с @a1exi4" +
                    "\n\nНомер телефона:"));
        }

    }

    public void recoveryPasswordEnterNumberPhone(Update update) {

        Long telegramUserId = update.message().chat().id();
        User user = userRepository.getUserByTelegramId(telegramUserId).get();

        if (Conditions.RECPASS_ENTER_PHONE_NUMBER.equals(user.getCondition())) {

            String phone = update.message().text();
            String urlRecovery = String.format(urlToSendCallRecovery, phone);

            JsonObject jsonObject = connectionGizmoService.connectionPost(connectionGizmoService.getToken(), urlRecovery);
            String token = "";

            user.setGizmoUserPhoneNumber(phone);

            try {
                token = jsonObject.get("result").getAsJsonObject().get("token").getAsString();
            } catch (UnsupportedOperationException e) {
                token = "null";
            }

            if ("null".equals(token)) {

                telegramBot.execute(new SendMessage(telegramUserId, "Номер не найден. Проверь номер и попробуй ещё раз"));
            } else {


                user.setCondition(Conditions.RECPASS_ENTER_FOUR_NUMBER_CODE);
                user.setGizmoTokenRecovery(token);
                userRepository.saveAndFlush(user);
                System.out.println("267 " + user.getGizmoTokenRecovery());
                telegramBot.execute(new SendMessage(telegramUserId, "Дождись звонка и введи последние 4 цифры номера" +
                        "\n4-значный код:"));
            }
        }
        return;
    }

    public void recoveryPasswordEnterFourNumberCode(Update update) {

        Long telegramUserId = update.message().chat().id();
        User user = userRepository.getUserByTelegramId(telegramUserId).get();

        if (Conditions.RECPASS_ENTER_FOUR_NUMBER_CODE.equals(user.getCondition())) {

            user.setGizmoCodeRecovery(update.message().text());
            user.setCondition(Conditions.RECPASS_ENTER_NEW_PASS);
            userRepository.saveAndFlush(user);

            telegramBot.execute(new SendMessage(telegramUserId, "Теперь введи новый пароль:"));
        }
        return;
    }

    public void recoveryPasswordEnterNewPassword(Update update) {

        Long telegramUserId = update.message().chat().id();
        User user = userRepository.getUserByTelegramId(telegramUserId).get();

        if (Conditions.RECPASS_ENTER_NEW_PASS.equals(user.getCondition())) {

            String pass = update.message().text();
            String token = user.getGizmoTokenRecovery();
            String code = user.getGizmoCodeRecovery();
            String url = String.format(urlToConfirmAndEnterNewPass, token, code, pass);

            JsonObject jsonObject = connectionGizmoService.connectionPost(connectionGizmoService.getToken(), url);

            int result = jsonObject.get("result").getAsInt();



            if (result == 0) {

                telegramBot.execute(new SendMessage(telegramUserId, "Твой пароль: " + "||" + pass + "||").parseMode(ParseMode.MarkdownV2));
                telegramBot.execute(new DeleteMessage(telegramUserId, update.message().messageId()));

                user.setCondition(Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN);
                userRepository.saveAndFlush(user);

                telegramBot.execute(new SendMessage(telegramUserId, "Теперь введи никнейм клубного аккаунта либо номер телефона:"));

            } else {

                user.setCondition(Conditions.RECPASS_ENTER_AGAIN_CONFIRM_CODE_OR_SEND_NEW_CONFIRM_CODE);
                userRepository.saveAndFlush(user);
                telegramBot.execute(new SendMessage(telegramUserId, "Введён неправильный 4-значный код" +
                        "\nПроверь код и введи его ещё раз либо получи новый - сделай выбор в меню ниже \uD83D\uDC47").replyMarkup(keyboardService.chooseRecoveryAcc()));
            }
        }
        return;
    }

    public void chooseEnterAgainConfirmCodeOrGetNewCode(Update update) {

        Long telegramUserId = update.message().chat().id();
        User user = userRepository.getUserByTelegramId(telegramUserId).get();

        if (Conditions.RECPASS_ENTER_AGAIN_CONFIRM_CODE_OR_SEND_NEW_CONFIRM_CODE.equals(user.getCondition())) {
            if (update.message().text().equalsIgnoreCase("Попробую ввести код ещё раз")) {

                telegramBot.execute(new SendMessage(telegramUserId, "Введи 4-значный код:"));
                user.setCondition(Conditions.RECPASS_ENTER_FOUR_NUMBER_CODE);
                userRepository.saveAndFlush(user);


            } else if (update.message().text().equalsIgnoreCase("Получить новый код")) {

                user.setCondition(Conditions.RECPASS_ENTER_NEW_PASS);
                userRepository.saveAndFlush(user);
                recoveryPasswordEnterNumberPhone(update);
            }
        }


    }

    public void waitGizmoPasswordCommand(Update update) {

        if ("/recpass".equals(update.message().text())) {

            recoveryPasswordCommandInfo(update);
            return;
        }

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
                        "\nПриходи в клуб каждый день, совершай покупки и получай награды!" +
                        "\n\nЧтобы использовать промокод, заюзай команду /promocode").replyMarkup(
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

        return;
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
        return;
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

                    sessionService.userActiveTime(update, user.getIsAdmin());

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

//    @PostConstruct
    public void sendUsersStats() {

        String regex = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$";
        Pattern pattern = Pattern.compile(regex);
        String spending = "/api/reports/users/spending?start=2021-01-01&end=2023-07-04";
        String userInfo = "/api/users";
        LocalDate start = LocalDate.of(2021, 1, 1);

        StringBuilder sb = new StringBuilder();
        JsonArray jsonArray = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), spending).getAsJsonArray("result");

        jsonArray.forEach(user -> {

            JsonObject userJO = user.getAsJsonObject();
            long userId = userJO.get("userId").getAsLong();
            String userName = userJO.get("username").getAsString();
            long userSpent = userJO.get("total").getAsLong();

            if (userSpent > 933) {

                String id = "/api/users/" + userId;
                JsonObject userArray = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), id).getAsJsonObject("result");

                try {
                    String phone = userArray.get("mobilePhone").getAsString();
                    Matcher matcher = pattern.matcher(phone);

                    if (matcher.matches()) {

                        phoneList.add(phone);
                    }

                } catch (UnsupportedOperationException ignored) {
                }
            }
        });

        Set<String> set = new HashSet<>(phoneList);
        phoneList.clear();
        phoneList.addAll(set);
        log.info("Create phoneList");
    }

    public void startPromo(Update update) {

        Long telegramUserId = update.message().chat().id();
        User user = userRepository.getUserByTelegramId(telegramUserId).get();

        if ("/promocode".equalsIgnoreCase(update.message().text())) {

            String url = "/api/users/" + user.getGizmoId();
            String mobilePhone = "";

            try {
                mobilePhone = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), url)
                        .getAsJsonObject("result").get("mobilePhone").getAsString();
            } catch (UnsupportedOperationException ignored) {

            }

            if (phoneList.contains(mobilePhone)) {
                if (user.getIsEnterPromo()) {

                    telegramBot.execute(new SendMessage(telegramUserId, "Вами был уже введён промокод \uD83D\uDE0E"));

                } else {

                    user.setCondition(Conditions.PROMO_START_ENTER);
                    userRepository.saveAndFlush(user);
                    telegramBot.execute(new SendMessage(telegramUserId, "Если у Вас есть промокод, то введите его:"));
                }
            } else {
                telegramBot.execute(new SendMessage(telegramUserId, "Нет доступных промокодов"));
            }
        }
        return;
    }

    public void enterPromo(Update update) {

        Long telegramUserId = update.message().chat().id();
        User user = userRepository.getUserByTelegramId(telegramUserId).get();

        if (promo.equals(update.message().text()) && Conditions.PROMO_START_ENTER.equals(user.getCondition())) {

            user.setIsEnterPromo(true);
            user.setCondition(Conditions.CHOOSE_PRIZE);
            userRepository.saveAndFlush(user);

            String awardUser = "/api/users/%s/points/%s";
            String urlAward = String.format(awardUser, user.getGizmoId(), 100);
            connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

            telegramBot.execute(new SendMessage(telegramUserId, "Вам было начислено 100 баллов"));
            telegramBot.execute(new SendMessage(telegramUserId, ""));
        }
        return;
    }

    public void test(Update update) {

        Long telegramUserId = update.message().chat().id();
        List<User> usersIsEnterPromo = userRepository.getUsersByIsEnterPromo(true).get();


        StringBuilder sb1 = new StringBuilder();
        sb1.append("13.07.2023-21.07.2023 period\n");
        StringBuilder sb2 = new StringBuilder();
        sb2.append("01.01.2021-13.07 period\n");
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Difference between second and first\n");

        usersIsEnterPromo.forEach(user -> {

            String url = String.format("/api/reports/users/spending?userId=%s&start=2023-07-13&end=2023-07-22", user.getGizmoId());
            String url2 = String.format("/api/reports/users/spending?userId=%s&start=2021-01-01&end=2023-07-12", user.getGizmoId());
            double totalSpendingInFirstPeriod = -1d;
            double totalSpendingInSecondPeriod = -1d;
            String gizmoName1 = "";
            String gizmoName2 = "";

            try {

                JsonObject result1 = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), url)
                        .getAsJsonArray("result").getAsJsonArray().get(0).getAsJsonObject();
                JsonObject result2 = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), url2)
                        .getAsJsonArray("result").getAsJsonArray().get(0).getAsJsonObject();

                totalSpendingInFirstPeriod =  result1.get("total").getAsDouble();
                totalSpendingInSecondPeriod =  result2.get("total").getAsDouble();
                gizmoName1 = result1.get("username").getAsString();
                gizmoName2 = result2.get("username").getAsString();

                sb1.append(gizmoName1).append(": ").append(totalSpendingInFirstPeriod).append("\n");
                sb2.append(gizmoName2).append(": ").append(totalSpendingInSecondPeriod).append("\n");
                sb3.append(gizmoName1).append(": ").append(totalSpendingInSecondPeriod - totalSpendingInFirstPeriod).append("\n");
            } catch (ClassCastException | IndexOutOfBoundsException ignored) {

            }




        });

        telegramBot.execute(new SendMessage(telegramUserId, sb1.append("----").toString()));
        telegramBot.execute(new SendMessage(telegramUserId, sb2.append("----").toString()));
        telegramBot.execute(new SendMessage(telegramUserId, sb3.toString()));
    }
}
