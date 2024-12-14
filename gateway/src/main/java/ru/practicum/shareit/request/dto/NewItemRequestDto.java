package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class NewItemRequestDto {

    @NotBlank
    private String description;

}
