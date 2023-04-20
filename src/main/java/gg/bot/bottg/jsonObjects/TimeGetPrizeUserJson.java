package gg.bot.bottg.jsonObjects;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;

@Data
public class TimeGetPrizeUserJson implements Serializable {

    private HashMap<String, LocalDateTime> timeGetPrizeUserJsonHashMap = new HashMap<>();
}
