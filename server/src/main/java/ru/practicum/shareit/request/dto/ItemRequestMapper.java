package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    ItemRequest dtoToItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto itemRequestToDto(ItemRequest itemRequest);

    List<ItemRequest> dtoListToItemRequestList(List<ItemRequestDto> itemRequestDto);

    List<ItemRequestDto> itemRequestListToDtoList(List<ItemRequest> itemRequestList);

}
