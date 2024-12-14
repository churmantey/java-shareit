package ru.practicum.shareit.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto) {
        log.info("GW CREATE user {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Positive @PathVariable Long userId,
                                             @RequestBody UserDto userDto) {
        log.info("GW UPDATE user id = {}, {}", userId, userDto);
        userDto.setId(userId);
        return userClient.updateUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@Positive @PathVariable Long userId) {
        log.info("GW GET user id = {}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GW GET all users");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("GW DELETE user id = {}", userId);
        userClient.deleteUserById(userId);
    }

}
