package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

public interface ItemRequestService {

    ItemRequestDto getRequest(Long requestId);

    ItemRequestDto addRequest(Long userId,  NewItemRequestDto newItemRequestDto);

    ItemRequestDto updateRequest(ItemRequestDto itemRequestDto);

    void deleteRequest(Long requestId);

}
