package gg.bot.bottg.service;

import gg.bot.bottg.condition.Conditions;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ConditionService {

    private final HashMap<Long, Conditions> userCondition;
    public ConditionService() {
        userCondition = new HashMap<>();
    }

    public void saveCondition(Long userId, Conditions condition) {
        userCondition.put(userId, condition);
    }

    public void updateCondition(Long userId, Conditions condition) {
        userCondition.put(userId, condition);
    }

    public HashMap<Long, Conditions> returnConditionUserById(Long userId) {
        HashMap<Long, Conditions> returnUser = new HashMap<>();
        if (userCondition.get(userId) != null) {
            returnUser.put(userId, userCondition.get(userId));
        }
        return returnUser;
    }

    public Conditions getCondition(Long userId) {
        return userCondition.get(userId);
    }
}
