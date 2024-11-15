package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

public interface ItemRequestService {


    ItemRequestDto getRequest(Long requestId);

    ItemRequestDto addRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto updateRequest(ItemRequestDto itemRequestDto);

    void deleteRequest(Long requestId);

}
