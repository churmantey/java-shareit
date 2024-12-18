package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User dtoToUser(UserDto userDto);

    UserDto userToDto(User user);

    List<User> dtoListToUserList(List<UserDto> userDto);

    List<UserDto> userListToDtoList(List<User> userDto);

}
