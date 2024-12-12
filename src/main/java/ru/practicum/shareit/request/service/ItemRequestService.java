package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto getItemRequest(Long requestId);

    ItemRequestDto createItemRequest(Long userId, NewItemRequestDto newItemRequestDto);

    List<ItemRequestDto> getUserItemRequests(Long userId);

    List<ItemRequestDto> getOtherItemRequests(Long userId);

    void deleteRequest(Long requestId);

}
