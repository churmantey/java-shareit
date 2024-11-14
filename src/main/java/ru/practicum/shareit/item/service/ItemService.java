package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItem(Long itemId);

    List<ItemDto> getAllItems();

    ItemDto createItem(ItemDto itemDto);

    ItemDto updateItem(ItemDto updatedItemDto);

    void deleteItemById(Long itemId);

}
