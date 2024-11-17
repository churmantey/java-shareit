package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepositoryImpl implements UserRepository {

    private final Map<Long, User> userStorage;

    @Override
    public User getElement(Long userId) {
        if (userId == null) {
            throw new ValidationException("User id of null received");
        }
        return Optional.ofNullable(userStorage.get(userId)).orElseThrow(
                () -> new NotFoundException("User with id = " + userId + " not found"));
    }

    @Override
    public User addElement(User user) {
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateElement(User newUser) {
        userStorage.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public void deleteElementById(Long userId) {
        if (userId == null) {
            throw new ValidationException("User id of null received");
        }
        User user = Optional.ofNullable(userStorage.remove(userId)).orElseThrow(
                () -> new NotFoundException("User with id = " + userId + " not found"));
    }

    @Override
    public List<User> getAllElements() {
        return userStorage.values().stream()
                .sorted(Comparator.comparingLong(User::getId))
                .toList();
    }

    @Override
    public User getUserByEmail(String email) {
        return userStorage.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

}
