package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.general.IdGenerator;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final IdGenerator idGenerator;
    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUser(Long userId) {
        return userMapper.userToDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found, " + userId)));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.userListToDtoList(userRepository.findAllByOrderByIdAsc());
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.dtoToUser(userDto);
        validateUser(user);
        return userMapper.userToDto(
                userRepository.save(user)
        );
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto updatedUserDto) {
        User user = userRepository.findById(updatedUserDto.getId())
                .orElseThrow(() -> new NotFoundException("User not found, " + updatedUserDto.getId()));
        if (updatedUserDto.getName() != null
                && !updatedUserDto.getName().isBlank()) user.setName(updatedUserDto.getName());
        if (updatedUserDto.getEmail() != null
                && !updatedUserDto.getEmail().isBlank()) user.setEmail(updatedUserDto.getEmail());
        validateUser(user);
        return userMapper.userToDto(
                userRepository.save(user)
        );
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found, " + userId));
        userRepository.deleteById(user.getId());
    }

    private void validateUser(User user) {
        if (user.getEmail() != null) {
            User existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser != null && !Objects.equals(existingUser.getId(), user.getId())) {
                throw new DuplicateException("User email already in use, " + user.getEmail());
            }
        }
    }

}
