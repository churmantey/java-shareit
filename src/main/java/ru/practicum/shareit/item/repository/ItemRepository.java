package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.general.BaseRepository;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemRepository extends BaseRepository<Item> {

    List<Item> findAvailableItemsByNameOrDescription(String substring);

    List<Item> findItemsByOwner(Long userId);

}
