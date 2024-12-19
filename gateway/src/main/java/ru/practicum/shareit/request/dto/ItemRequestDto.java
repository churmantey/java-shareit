package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ItemRequestDto {

    private Long id;

    private String description;

    private Long author;

    private LocalDateTime created;

    private List<ItemForRequestDto> items = new ArrayList<>();

}
