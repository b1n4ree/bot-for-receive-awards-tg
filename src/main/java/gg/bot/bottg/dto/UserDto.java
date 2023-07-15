package gg.bot.bottg.dto;

import gg.bot.bottg.enums.Conditions;
import gg.bot.bottg.jsonObjects.PrizeJson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserDto {

    Long id;
    Long telegramId;
    Long currentStreakDay;
    Long maxStreakDay;
    Long moneySpentInDay;
    Long moneySpentInPrevDay;
    Long gizmoId;
    String telegramFirstName;
    String telegramSecondName;
    String gizmoName;
    LocalDateTime dateRegistration;
    Boolean authorizationInGizmoAccount;
    Conditions condition;
    PrizeJson prizeJson;
    LocalDateTime dateGetPreviousPrize;
    Boolean isZeroingStreak;
    Boolean isDeleted;
    Boolean isAdmin;

}
