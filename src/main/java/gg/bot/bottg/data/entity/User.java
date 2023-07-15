package gg.bot.bottg.data.entity;

import gg.bot.bottg.enums.Conditions;
import gg.bot.bottg.jsonObjects.PrizeJson;
import gg.bot.bottg.jsonObjects.TimeGetPrizeUserJson;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.time.ZoneId;



@Table(name = "users")
@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "telegram_id", unique = true)
    private Long telegramId;

    @Column(name = "current_streak_day")
    private Long currentStreakDay = 0L;

    @Column(name = "max_streak_day")
    private Long maxStreakDay = 0L;

    @Column(name = "money_spent_in_day")
    private Long moneySpentInDay = 0L;

    @Column(name = "money_spent_in_previous_day")
    private Long moneySpentInPreviousDay = 0L;

    @Column(name = "gizmo_id")
    private Long gizmoId = 0L;

    @Column(name = "telegram_first_name")
    private String telegramFirstName;

    @Column(name = "telegram_second_name")
    private String telegramSecondName;

    @Column(name = "telegram_nickname")
    private String telegramNickname;

    @Column(name = "gizmo_name")
    private String gizmoName = "gizmoNameStart";

    @Column(name = "gizmo_token_recovery")
    private String gizmoTokenRecovery;

    @Column(name = "gizmo_code_recovery")
    private String gizmoCodeRecovery;

    @Column(name = "gizmo_user_phone_number")
    private String gizmoUserPhoneNumber;

    @Column(name = "date_registration")
    private LocalDateTime dateRegistration = LocalDateTime.now(ZoneId.of("UTC+3"));

    @Column(name = "authorization_in_gizmo_account")
    private Boolean authorizationInGizmoAccount = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_user_conditi")
    private Conditions condition;

    @Column(name = "prize")
    @JdbcTypeCode(SqlTypes.JSON)
    private PrizeJson prizeJson = new PrizeJson();

    @Column(name = "time_receive_prizes")
    @JdbcTypeCode(SqlTypes.JSON)
    private TimeGetPrizeUserJson timeGetPrizeUserJson = new TimeGetPrizeUserJson();

    @Column(name = "date_get_previous_prize")
    private LocalDateTime dateGetPreviousPrize;

    @Column(name = "is_zeroing_streak")
    private Boolean isZeroingStreak = false;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "is_admin")
    private Boolean isAdmin = false;

    @Column(name = "is_enter_promo")
    private Boolean isEnterPromo = false;
}
