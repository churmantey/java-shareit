package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    public User dtoToUser (UserDto userDto);

    public UserDto userToDto (User user);

    public List<User> dtoListToUserList (List<UserDto> userDto);
    public List<UserDto> userListToDtoList (List<User> userDto);

}
