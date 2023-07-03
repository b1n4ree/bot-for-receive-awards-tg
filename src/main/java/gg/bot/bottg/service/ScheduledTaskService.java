package gg.bot.bottg.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import gg.bot.bottg.data.entity.User;
import gg.bot.bottg.data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ScheduledTaskService {

    @Value("${telegram_id_al}")
    private Long myTelegramId;

    @Autowired
    UserRepository userRepository;
    private final TelegramBot telegramBot;

    public ScheduledTaskService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Scheduled(fixedRate = 86400000)
    @Async
    public void someTest() {

        log.info("Current time: " + LocalDateTime.now(ZoneId.of("UTC+3")));
        Optional<List<User>> usersOptional = userRepository.getUsersByAuthorizationInGizmoAccount(true);

        if (usersOptional.isPresent()) {

            List<User> userWithGizmoAcc = usersOptional.get();
            userWithGizmoAcc.forEach(user -> {

                LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                LocalDate dateLastGetPrize;
                try {
                    dateLastGetPrize = user.getDateGetPreviousPrize().toLocalDate();
                } catch (NullPointerException e) {
                    dateLastGetPrize = currentDate;
                }
                long betweenWithCurrentAndLastGetPrizeDate = currentDate.toEpochDay() - dateLastGetPrize.toEpochDay();

                if (betweenWithCurrentAndLastGetPrizeDate == 1) {
                    telegramBot.execute(new SendMessage(user.getTelegramId(), "Не прерывай стрик! Успей получить " +
                            "награду сегодня" +
                            "\nТекущий стрик " + user.getCurrentStreakDay()).disableNotification(true));
                }
            });
        }
    }
}
