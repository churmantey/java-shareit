package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.general.IdGenerator;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final IdGenerator idGenerator;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Override
    public ItemDto getItem(Long itemId) {
        return itemMapper.itemToDto(itemRepository.getElement(itemId));
    }

    @Override
    public List<ItemDto> getAllItems() {
        return itemMapper.itemListToDtoList(itemRepository.getAllElements());
    }

    @Override
    public ItemDto createItem(ItemDto itemDto) {
        validateItem(itemDto);
        Item item = itemMapper.dtoToItem(itemDto);
        item.setId(idGenerator.getNextId());

        return itemMapper.itemToDto(
                itemRepository.addElement(item)
        );
    }

    @Override
    public ItemDto updateItem(ItemDto updatedItemDto) {
        return null;
    }

    @Override
    public void deleteItemById(Long itemId) {

    }

    private void validateItem(ItemDto itemDto) {
        if (itemDto.getOwner() != null) {
            UserDto owner = userService.getUser(itemDto.getOwner());
            if (owner == null) {
                throw new NotFoundException("Item owner with id = " + itemDto.getOwner() + " not found");
            }
        }
    }


}
