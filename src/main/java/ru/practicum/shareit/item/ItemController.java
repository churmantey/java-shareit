package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) Long userId,
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
    public List<ItemDto> getItemsByOwner(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET user items {}", userId);
        return itemService.getItemsByOwner(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                              @PathVariable Long itemId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("UPDATE item {}, user {}, item data {}", itemId, userId, itemDto);
        itemDto.setId(itemId);
        itemDto.setOwner(userId);
        return itemService.updateItem(itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsByText(@NotNull @RequestParam String text) {
        log.info("Search available items {}", text);
        return itemService.findAvailableItemsByText(text);
    }

}
