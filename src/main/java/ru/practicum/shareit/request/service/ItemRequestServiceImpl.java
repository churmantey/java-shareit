package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestJpaRepository requestRepository;
    private final ItemRequestMapper requestMapper;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long userId, NewItemRequestDto newItemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setAuthor(userId);
        itemRequest.setDescription(newItemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        return requestMapper.itemRequestToDto(requestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getItemRequest(Long requestId) {
        ItemRequest itemRequest = getRequestById(requestId);
        return requestMapper.itemRequestToDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUserItemRequests(Long userId) {
        return
                requestRepository.;
    }

    @Override
    public List<ItemRequestDto> getOtherItemRequests(Long userId) {
        return null;
    }

    @Override
    @Transactional
    public void deleteRequest(Long requestId) {
        requestRepository.deleteById(requestId);
    }

    private ItemRequest getRequestById(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest not found, id = " + requestId));
    }

}
