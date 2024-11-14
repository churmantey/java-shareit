package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.general.BaseRepository;
import ru.practicum.shareit.user.User;

public interface UserRepository extends BaseRepository<User> {

    User getUserByEmail (String email);

}
