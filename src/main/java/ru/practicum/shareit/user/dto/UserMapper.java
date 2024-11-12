package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public final class UserMapper {

    public User dtoToUser (UserDto userDto) {
        User user = new User();
        return user;
    }

    public UserDto userToDto (User user) {
        UserDto userDto = new UserDto();
        return userDto;
    }


}
