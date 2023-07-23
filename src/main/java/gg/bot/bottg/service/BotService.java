package gg.bot.bottg.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import gg.bot.bottg.enums.Conditions;
import gg.bot.bottg.data.entity.User;
import gg.bot.bottg.data.repository.PrizeRepository;
import gg.bot.bottg.data.repository.UserRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


@Slf4j
@Component
public class BotService implements UpdatesListener {

    @Autowired
    private final PrizeRepository prizeRepository;

    public static final String ANSI_GREEN = "\u001B[32m";

    @Value("${gizmo_url}")
    private String gizmoUrl;

    @Value("${gizmo_login}")
    private String gizmoLogin;

    @Value("${gizmo_password}")
    private String gizmoPassword;

    @Value("${gizmo_users_spending_url_test}")
    private String usersSpendingUrlTest;

    @Value("${gizmo_users_spending_custom_url}")
    private String usersSpendingCustomUrl;

    @Value("${gizmo_user_id}")
    private String userIdGizmo;

    @Value("${gizmo_user_password_valid}")
    private String gizmoUserValid;

    @Value("${telegram_id_al}")
    private Long myTelegramId;

    private final TelegramBot telegramBot;

    private final UserRepository userRepository;
    private final KeyboardService keyboardService;
    private final CallBackService callBackService;
    private final CommandService commandService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private final SessionService sessionService;

    @Setter
    private String token;


    public BotService(TelegramBot telegramBot, KeyboardService keyboardService, ConnectionGizmoService connectionGizmoService,
                      UserRepository userRepository, CallBackService callBackService, CommandService commandService,
                      PrizeRepository prizeRepository, SessionService sessionService) {
        this.userRepository = userRepository;
        this.telegramBot = telegramBot;
        this.keyboardService = keyboardService;
        this.callBackService = callBackService;
        this.commandService = commandService;
        this.sessionService = sessionService;
        telegramBot.setUpdatesListener(this);
        this.prizeRepository = prizeRepository;
    }
    public <T extends BaseRequest<T, R>, R extends BaseResponse> R execute(BaseRequest<T, R> request) {
        return telegramBot.execute(request);
    }

    @Override
    public int process(List<Update> list) {
        long start = System.currentTimeMillis();
        list.forEach(update -> {

            callBackService.callbackHandler(update);

            if (update.message() != null) {

                Long telegramUserId = update.message().from().id();

                if (update.message().text() != null) {

//                    commandService.sendUsersStats(update);

                    commandService.test(update);

                    commandService.changePrizeName(update);
                    commandService.startCommand(update);

                    Optional<User> userOptional = userRepository.getUserByTelegramId(telegramUserId);
                    User user = new User();
                    if (userOptional.isEmpty()) {
                        log.info("User is empty");
                    } else {
                        user = userOptional.get();
                    }
                    System.out.println(user.getCondition());
                    if (user.getCondition() != null) {

                        if (Conditions.START.equals(user.getCondition())) {

                            commandService.yesOrNoCommand(update);

                        } else if (Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN.equals(user.getCondition())) {

                            commandService.waitGizmoLoginCommand(update);

                        } else if (Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_PASS.equals(user.getCondition())) {

                            commandService.waitGizmoPasswordCommand(update);

                        } else if (Conditions.AGAIN_ENTER_LOGIN_PASSWORD.equals(user.getCondition())) {

                            commandService.waitGizmoPasswordCommand(update);

                        } else if (Conditions.RECPASS_ENTER_PHONE_NUMBER.equals(user.getCondition())) {

                            commandService.recoveryPasswordEnterNumberPhone(update);

                        } else if (Conditions.RECPASS_ENTER_FOUR_NUMBER_CODE.equals(user.getCondition())) {

                            commandService.recoveryPasswordEnterFourNumberCode(update);

                        } else if (Conditions.RECPASS_ENTER_NEW_PASS.equals(user.getCondition())) {

                            commandService.recoveryPasswordEnterNewPassword(update);

                        } else if (Conditions.PROMO_START_ENTER.equals(user.getCondition())) {

                            commandService.enterPromo(update);

                        } else if (Conditions.RECPASS_ENTER_AGAIN_CONFIRM_CODE_OR_SEND_NEW_CONFIRM_CODE.equals(user.getCondition())) {

                            commandService.chooseEnterAgainConfirmCodeOrGetNewCode(update);

                        } else {

                            commandService.sendAllUsersIdAndName(update);
                            commandService.sendMessageToUser(update);
                            commandService.getPrizeInlineKeyboard(update);
                            commandService.getComputersSessions(update);
                            commandService.sendMessageToAllUsers(update);
                            commandService.recoveryPasswordCommandInfo(update);
                            commandService.startPromo(update);
//                            commandService.deleteUserData(update);
                        }
                    }
                }
            }
        });
        long end = System.currentTimeMillis();
        System.out.println(end - start + "ms");
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
