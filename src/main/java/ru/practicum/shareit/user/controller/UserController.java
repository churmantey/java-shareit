package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {


    }

    @PutMapping
    public UserDto updateUser(@RequestBody UserDto userDto) {


    }

    @GetMapping
    public UserDto getUser() {

    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {

    }

    private validateUser(UserDto userDto)

}
