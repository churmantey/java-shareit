package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
public class ItemRequestDto {

    @Positive
    private Long id;

    @NotBlank
    private String description;

    @Positive
    private Long requestor;

    @NotNull
    private LocalDateTime created;

}
