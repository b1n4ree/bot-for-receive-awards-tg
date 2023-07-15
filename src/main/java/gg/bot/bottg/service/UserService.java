package gg.bot.bottg.service;

import gg.bot.bottg.data.entity.User;
import gg.bot.bottg.data.repository.UserRepository;
import gg.bot.bottg.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto getUserByGizmoName(String gizmoName) {

        Optional<User> userOpt = userRepository.getUserByGizmoName(gizmoName);
        UserDto userDto = new UserDto();

        if (userOpt.isPresent()) {

            User user = userOpt.get();

            userDto.setId(user.getId());
            userDto.setTelegramId(user.getTelegramId());
            userDto.setCurrentStreakDay(user.getCurrentStreakDay());
            userDto.setMaxStreakDay(user.getMaxStreakDay());
            userDto.setMoneySpentInDay(user.getMoneySpentInDay());
            userDto.setMoneySpentInPrevDay(user.getMoneySpentInPreviousDay());
            userDto.setGizmoId(user.getGizmoId());
            userDto.setTelegramFirstName(user.getTelegramFirstName());
            userDto.setTelegramSecondName(user.getTelegramSecondName());
            userDto.setGizmoName(user.getGizmoName());
            userDto.setDateRegistration(user.getDateRegistration());
            userDto.setAuthorizationInGizmoAccount(user.getAuthorizationInGizmoAccount());
            userDto.setCondition(user.getCondition());
            userDto.setPrizeJson(user.getPrizeJson());
            userDto.setDateGetPreviousPrize(user.getDateGetPreviousPrize());
            userDto.setIsZeroingStreak(user.getIsZeroingStreak());
            userDto.setIsDeleted(user.getIsDeleted());
            userDto.setIsAdmin(user.getIsAdmin());

        } else {
            return new UserDto();
        }
        return userDto;
    }

//    public List<UserDto> getUserByCustomParam(int topNumber, String attribute) {
//
//        if (attribute != null) {
//            switch (attribute) {
//                case "current-streak-day":
//                    return new ArrayList<UserDto>();
//            }
//        }
//    }
}
