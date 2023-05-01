package gg.bot.bottg.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pengrad.telegrambot.model.Update;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "user_and_message_statistic")
@Entity
@Data
public class UserAndMessageStatistic {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "telegram_user_id")
    private Long telegramUserId;

    @Column(name = "timeOfAction")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timeOfAction;

    @Column(name = "description_action")
    private String descriptionAction;

    @Column(name = "message_telegram_text")
    private String messageTelegramText;

    @Column(name = "")
    private Long countUserUsagePerDay = 0L;

    @Column(name = "receive_prize_per_day")
    private Long receivePrizePerDay;
}
