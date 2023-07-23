package gg.bot.bottg.data.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Table(name = "callbackday_receive_award")
@Entity
@Data
public class CallbackDayReceiveAward {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "date")
    private LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("UTC+3"));

    @Column(name = "day")
    private int day;

    @Column(name = "gizmo_name")
    private String gizmoName;

    @Column(name = "gizmo_id")
    private Long gizmoId;

    @Column(name = "telegram_firstname")
    private String telegramFirstname;

    @Column(name = "telegram_secondname")
    private String telegramSecondname;

    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(name = "telegram_nickname")
    private String telegramNickname;
}
