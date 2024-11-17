package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {

    @Positive
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    @Positive
    private Long owner; // id владельца

    private Long request; // id запроса

}
