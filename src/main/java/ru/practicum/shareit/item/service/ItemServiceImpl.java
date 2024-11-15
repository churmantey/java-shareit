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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

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
    public List<ItemDto> getItemsByOwner(Long userId) {
        UserDto userDto = userService.getUser(userId);
        return itemMapper.itemListToDtoList(
                itemRepository.findItemsByOwner(userDto.getId())
        );
    }

    @Override
    public ItemDto createItem(ItemDto itemDto) {
        Item item = itemMapper.dtoToItem(itemDto);
        validateItem(item);
        item.setId(idGenerator.getNextId());

        return itemMapper.itemToDto(
                itemRepository.addElement(item)
        );
    }

    @Override
    public ItemDto updateItem(ItemDto updatedItemDto) {
        Item item = itemRepository.getElement(updatedItemDto.getId());
        if (updatedItemDto.getName() != null
                && !updatedItemDto.getName().isBlank()) item.setName(updatedItemDto.getName());
        if (updatedItemDto.getDescription() != null
                && !updatedItemDto.getDescription().isBlank()) item.setDescription(updatedItemDto.getDescription());
        if (updatedItemDto.getAvailable() != null)
            item.setAvailable(updatedItemDto.getAvailable());
        validateItem(item);
        return itemMapper.itemToDto(
                itemRepository.updateElement(item)
        );
    }

    @Override
    public void deleteItemById(Long itemId) {
        itemRepository.deleteElementById(itemId);
    }

    @Override
    public List<ItemDto> findAvailableItemsByText(String text) {
        if (text.isBlank()) return new ArrayList<>();
        return itemMapper.itemListToDtoList(
                itemRepository.findAvailableItemsByNameOrDescription(text)
        );
    }

    private void validateItem(Item item) {
        if (item == null) throw new ValidationException("Item of null received");
        if (item.getName() == null
                || item.getName().isBlank()) throw new ValidationException("Item name cannot be empty");
        if (item.getDescription() == null
                || item.getDescription().isBlank()) throw new ValidationException("Item description cannot be empty");
        if (item.getAvailable() == null) throw new ValidationException("Item without available field received");
        if (item.getOwner() != null) {
            UserDto owner = userService.getUser(item.getOwner());
            if (owner == null) {
                throw new NotFoundException("Item owner with id = " + item.getOwner() + " not found");
            }
        }
    }

}
