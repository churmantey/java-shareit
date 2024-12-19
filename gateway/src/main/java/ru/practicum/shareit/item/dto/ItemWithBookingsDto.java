package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ItemWithBookingsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner; // id владельца
    private Long request; // id запроса
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentDto> comments = new ArrayList<>();
}
