package gg.bot.bottg.dto;

import gg.bot.bottg.condition.Conditions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserDto {

    private Long telegramId;
    private String name;
    private String nickname;
    private LocalDate date;
    private Conditions condition;
}
