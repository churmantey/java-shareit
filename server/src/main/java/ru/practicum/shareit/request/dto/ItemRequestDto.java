package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {

    private Long id;

    private String description;

    private Long author;

    private LocalDateTime created;

    List<ItemForRequestDto> items = new ArrayList<>();

}
