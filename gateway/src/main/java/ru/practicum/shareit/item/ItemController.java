package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;

import static ru.practicum.shareit.Headers.USER_ID_HEADER;

@Slf4j
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("GW CREATE item {}, owner {}", itemDto, userId);
        itemDto.setOwner(userId);
        return itemClient.createItem(itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId) {
        log.info("GW GET item {}", itemId);
        return itemClient.getItem(itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GW GET user items {}", userId);
        return itemClient.getItemsByOwner(userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("GW UPDATE item {}, user {}, item data {}", itemId, userId, itemDto);
        itemDto.setId(itemId);
        itemDto.setOwner(userId);
        return itemClient.updateItem(itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemsByText(@NotBlank @RequestParam String text) {
        log.info("GW Search available items {}", text);
        return itemClient.findItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody NewCommentDto newCommentDto) {
        log.info("GW ADD COMMENT item {}, user {}, comment data {}", itemId, userId, newCommentDto);
        return itemClient.addComment(userId, itemId, newCommentDto);
    }

}
