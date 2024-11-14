package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.general.IdGenerator;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final IdGenerator idGenerator;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUser(Long userId) {
        return userMapper.userToDto(userRepository.getElement(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.userListToDtoList(userRepository.getAllElements());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        validateUser(userDto);
        User user = userMapper.dtoToUser(userDto);
        user.setId(idGenerator.getNextId());

        return userMapper.userToDto(
                userRepository.addElement(user)
        );
    }

    @Override
    public UserDto updateUser(UserDto updatedUserDto) {
        validateUser(updatedUserDto);
        User user = userRepository.getElement(updatedUserDto.getId());
        if (updatedUserDto.getName() != null
                && !updatedUserDto.getName().isBlank()) user.setName(updatedUserDto.getName());
        if (updatedUserDto.getEmail() != null
                &&!updatedUserDto.getEmail().isBlank()) user.setEmail(updatedUserDto.getEmail());
        return userMapper.userToDto(
            userRepository.updateElement(user)
        );
    }

    @Override
    public void deleteUserById(Long userId) {
        User user = userRepository.getElement(userId);
        userRepository.deleteElementById(user.getId());
    }

    private void validateUser(UserDto userDto) {
        if (userDto.getEmail() != null) {
            User existingUser = userRepository.getUserByEmail(userDto.getEmail());
            if (existingUser != null && !Objects.equals(existingUser.getId(), userDto.getId())) {
                throw new ValidationException("User email already in use");
            }
        }
    }

}
