package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.Comment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner; // id владельца
    private Long request; // id запроса
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<Comment> comments;
}
