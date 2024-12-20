package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Comment;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItemIdOrderByCreated(Long itemId);

}
