package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("CREATE user {}", userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Positive @PathVariable Long userId,
                              @RequestBody UserDto userDto) {
        log.info("UPDATE user id = {}, {}", userId, userDto);
        UserDto existingUser = userService.getUser(userId);
        userDto.setId(userId);
        return userService.updateUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("GET user id = {}", userId);
        return userService.getUser(userId);
    }
    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("GET all users");
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("DELETE user id = {}", userId);
        userService.deleteUserById(userId);
    }

}
