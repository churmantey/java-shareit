package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import static ru.practicum.shareit.Headers.USER_ID_HEADER;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    // POST /requests
    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                    @RequestBody NewItemRequestDto newItemRequestDto) {
        log.info("GW CREATE item request {}", newItemRequestDto);
        return requestClient.createItemRequest(userId, newItemRequestDto);
    }

    // GET /requests
    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GW GET user {} item request", userId);
        return requestClient.getUserItemRequests(userId);
    }

    // GET /requests/all
    @GetMapping("/all")
    public ResponseEntity<Object> getOtherItemRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GW GET others item request for user {}", userId);
        return requestClient.getOtherItemRequests(userId);
    }

    // GET /requests/{requestId}
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable Long requestId) {
        log.info("GW GET item request, id = {}", requestId);
        return requestClient.getItemRequest(requestId);
    }

}
