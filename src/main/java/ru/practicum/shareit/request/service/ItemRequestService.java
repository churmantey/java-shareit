package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

public interface ItemRequestService {

    ItemRequestDto getItemRequest(Long requestId);

    ItemRequestDto createItemRequest(Long userId, NewItemRequestDto newItemRequestDto);

    void deleteRequest(Long requestId);

}
