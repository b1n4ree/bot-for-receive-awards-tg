package gg.bot.bottg.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import gg.bot.bottg.condition.Conditions;
import gg.bot.bottg.data.entity.User;
import gg.bot.bottg.data.repository.UserRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


@Slf4j
@Component
public class BotService implements UpdatesListener {

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

    private final TelegramBot telegramBot;

    private final UserRepository userRepository;
    private final KeyboardService keyboardService;
    private final CallBackService callBackService;
    private final ConditionService conditionService;
    private final CommandService commandService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    @Setter
    private String token;


    public BotService(TelegramBot telegramBot, KeyboardService keyboardService, ConnectionGizmoService connectionGizmoService,
                      UserRepository userRepository, CallBackService callBackService, ConditionService conditionService, CommandService commandService) {
        this.userRepository = userRepository;
        this.telegramBot = telegramBot;
        this.keyboardService = keyboardService;
        this.callBackService = callBackService;
        this.conditionService = conditionService;
        this.commandService = commandService;
        telegramBot.setUpdatesListener(this);
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

                    commandService.startCommand(update);

                    if (conditionService.getCondition(telegramUserId) != null) {

                        User user = userRepository.getUserByTelegramId(telegramUserId).get();

                        if (Conditions.START.equals(user.getCondition())
                                || Conditions.START.equals(conditionService.getCondition(telegramUserId))) {

                            commandService.yesOrNoCommand(update);

                        } else if (Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN.equals(user.getCondition())
                                || Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_LOGIN.equals(conditionService.getCondition(telegramUserId))) {

                            commandService.waitGizmoLoginCommand(update);

                        } else if (Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_PASS.equals(user.getCondition())
                                || Conditions.SELECTED_STREAK_AND_WAIT_GIZMO_PASS.equals(conditionService.getCondition(telegramUserId))) {

                            commandService.waitGizmoPasswordCommand(update);

                        } else if (Conditions.AGAIN_ENTER_LOGIN_PASSWORD.equals(user.getCondition())
                                || Conditions.AGAIN_ENTER_LOGIN_PASSWORD.equals(conditionService.getCondition(telegramUserId))) {

                            commandService.waitGizmoPasswordCommand(update);
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
