package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner; // id владельца
    private Long request; // id запроса
}
