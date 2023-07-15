package gg.bot.bottg.controller;

import gg.bot.bottg.data.repository.UserRepository;
import gg.bot.bottg.dto.UserDto;
import gg.bot.bottg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @RequestMapping(value = "/get-user-by-gizmo-name={name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getUserByGizmoName(@PathVariable String name) {
        return ResponseEntity.ok().body(userService.getUserByGizmoName(name));
    }

//    @RequestMapping(value = "/get-top-users-by-number={number}&attribute={attribute}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<UserDto>> getTopUsersBy(@PathVariable int number, @PathVariable String attribute) {
//        return ResponseEntity.ok().body();
//    }
}
