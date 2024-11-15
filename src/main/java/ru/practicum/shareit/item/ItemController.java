package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("CREATE item {}, owner {}", itemDto, userId);
        itemDto.setOwner(userId);
        return itemService.createItem(itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("GET item {}", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping()
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET user items {}", userId);
        return itemService.getItemsByOwner(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("UPDATE item {}, user {}, item data {}", itemId, userId, itemDto);
        ItemDto existingItem = itemService.getItem(itemId);
        if (!existingItem.getOwner().equals(userId)) {
            throw new AccessException("User with id = " + userId +
                    " can't update item with id = " + itemId);
        }
        itemDto.setId(existingItem.getId());
        return itemService.updateItem(itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsByText(@NotNull @RequestParam String text) {
        log.info("Search available items {}", text);
        return itemService.findAvailableItemsByText(text);
    }

}
