package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    List<User> findAllByOrderByIdAsc();

    User findByEmail(String email);

}
