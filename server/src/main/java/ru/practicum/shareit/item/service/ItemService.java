package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.dto.NewCommentDto;

import java.util.List;

public interface ItemService {

    ItemWithBookingsDto getItem(Long itemId);

    List<ItemWithBookingsDto> getItemsByOwner(Long userId);

    List<ItemDto> findAvailableItemsByText(String searchQuery);

    ItemDto createItem(ItemDto itemDto);

    ItemDto updateItem(ItemDto updatedItemDto);

    void deleteItemById(Long itemId);

    CommentDto addComment(Long userId, Long itemId, NewCommentDto newCommentDto);

}
