package gg.bot.bottg.data.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Table(name = "actions_user")
@Data
@Entity
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_telegram_id")
    private Long userTelegramId;

    @Column(name = "user_telegram_nickname")
    private String userTelegramNickname;

    @Column(name = "current_streak_day")
    private Long currentStreakDay;

    @Column(name = "money_spent_in_current_day")
    private Long moneySpent;

    @Column(name = "action")
    private String action;

    @Column(name = "date")
    private LocalDateTime date = LocalDateTime.now(ZoneId.of("UTC+3"));
}

