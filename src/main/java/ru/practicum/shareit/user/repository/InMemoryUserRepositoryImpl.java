package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepositoryImpl implements UserRepository {

    private final Map<Long, User> userStorage;

    @Override
    public User getElement(Long id) {
        return null;
    }

    @Override
    public User addElement(User element) {
        return null;
    }

    @Override
    public User updateElement(User newElement) {
        return null;
    }

    @Override
    public void deleteElementById(Long id) {

    }

    @Override
    public List<User> getAllElements() {
        return null;
    }
}
