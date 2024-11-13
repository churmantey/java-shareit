package ru.practicum.shareit.item.model;

import lombok.Data;
import org.apache.coyote.Request;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Request request;
}
