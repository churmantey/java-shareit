package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> itemStorage;

    @Override
    public Item getElement(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Item id of null received");
        }
        if (itemStorage.containsKey(itemId)) return itemStorage.get(itemId);
        else throw new NotFoundException("Item with id = " + itemId + " not found");
    }

    @Override
    public Item addElement(Item item) {
        itemStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateElement(Item updatedItem) {
        itemStorage.put(updatedItem.getId(), updatedItem);
        return updatedItem;
    }

    @Override
    public void deleteElementById(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Item id of null received");
        }
        if (itemStorage.containsKey(itemId)) itemStorage.remove(itemId);
        else throw new NotFoundException("Item with id = " + itemId + " not found");

    }

    @Override
    public List<Item> getAllElements() {
        return itemStorage.values().stream()
                .sorted(Comparator.comparingLong(Item::getId))
                .toList();
    }

    @Override
    public List<Item> findAvailableItemsByNameOrDescription(String substring) {
        return itemStorage.values().stream()
                .filter(item -> item.getAvailable()
                        && (item.getName().toLowerCase().contains(substring.toLowerCase())
                        || item.getDescription().toLowerCase().contains(substring.toLowerCase())))
                .toList();
    }

    @Override
    public List<Item> findItemsByOwner(Long userId) {
        return itemStorage.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .toList();
    }
}
