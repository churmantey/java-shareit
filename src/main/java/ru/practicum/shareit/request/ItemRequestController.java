package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.general.Headers.USER_ID_HEADER;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    // POST /requests
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ItemRequestDto createItemRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                     @Valid @RequestBody NewItemRequestDto newItemRequestDto) {
        return requestService.createItemRequest(userId, newItemRequestDto);
    }

    // GET /requests
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<ItemRequestDto> getUserItemRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.getUserItemRequests(userId);
    }

    // GET /requests/all
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    List<ItemRequestDto> getOtherItemRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.getOtherItemRequests(userId);
    }

    // GET /requests/{requestId}
    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    ItemRequestDto getItemRequest(@PathVariable Long requestId) {
        return requestService.getItemRequest(requestId);
    }

}
