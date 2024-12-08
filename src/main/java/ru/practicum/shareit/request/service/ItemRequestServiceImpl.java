package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestJpaRepository requestRepository;
    private final ItemRequestMapper requestMapper;

    @Override
    @Transactional
    public ItemRequestDto addRequest(Long userId, NewItemRequestDto newItemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setAuthor(userId);
        itemRequest.setDescription(newItemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        return requestMapper.itemRequestToDto(requestRepository.save(itemRequest));
    }


}
