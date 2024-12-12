package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestJpaRepository requestRepository;
    private final ItemRequestMapper requestMapper;
    private final ItemJpaRepository itemRepository;
    private final ItemMapper itemMapper;

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
        ItemRequestDto itemRequestDto = requestMapper.itemRequestToDto(getRequestById(requestId));
        fillItems(itemRequestDto);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getUserItemRequests(Long userId) {
        List<ItemRequestDto> initialList = requestMapper.itemRequestListToDtoList(
                requestRepository.findByAuthorOrderByCreated(userId));
        return initialList.stream()
                .peek(this::fillItems)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getOtherItemRequests(Long userId) {
        return requestMapper.itemRequestListToDtoList(requestRepository.findByAuthorNotOrderByCreated(userId));
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

    private void fillItems(ItemRequestDto itemRequestDto) {
        itemRequestDto.setItems(
                itemRepository.findByRequestId(itemRequestDto.getId()).stream()
                .map(itemMapper::itemToItemForRequestDto)
                .toList()
        );
    }

}
