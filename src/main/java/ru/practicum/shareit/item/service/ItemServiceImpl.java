package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.general.LastNextBookingDates;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemJpaRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final BookingJpaRepository bookingRepository;

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found, " + itemId));
        fillComments(item);
        return itemMapper.itemToDto(item);
    }

    @Override
    public List<ItemDto> getAllItems() {
        return itemMapper.itemListToDtoList(itemRepository.findAllByOrderByIdAsc());
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        UserDto userDto = userService.getUser(userId);
        return itemMapper.itemListToDtoList(
                itemRepository.findAllByOwnerOrderByIdAsc(userDto.getId())
                        .stream()
                        .peek(this::fillBookingDates)
                        .peek(this::fillComments)
                        .toList()
        );
    }

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto) {
        Item item = itemMapper.dtoToItem(itemDto);
        validateItem(item);
        return itemMapper.itemToDto(
                itemRepository.save(item)
        );
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto updatedItemDto) {
        Item item = itemRepository.findById(updatedItemDto.getId())
                .orElseThrow(() -> new NotFoundException("Item not found, " + updatedItemDto.getId()));
        if (!item.getOwner().equals(updatedItemDto.getOwner())) {
            throw new AccessException("User with id = " + updatedItemDto.getOwner() +
                    " can't update item with id = " + updatedItemDto.getId());
        }
        if (updatedItemDto.getName() != null
                && !updatedItemDto.getName().isBlank()) item.setName(updatedItemDto.getName());
        if (updatedItemDto.getDescription() != null
                && !updatedItemDto.getDescription().isBlank()) item.setDescription(updatedItemDto.getDescription());
        if (updatedItemDto.getAvailable() != null)
            item.setAvailable(updatedItemDto.getAvailable());
        validateItem(item);
        return itemMapper.itemToDto(
                itemRepository.save(item)
        );
    }

    @Override
    @Transactional
    public void deleteItemById(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> findAvailableItemsByText(String text) {
        if (text.isBlank()) return new ArrayList<>();
        return itemMapper.itemListToDtoList(
                itemRepository.findAvailableByNameOrDescription(text)
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

    private void fillComments(Item item) {
        item.setComments(new ArrayList<>());
    }
    private void fillBookingDates(Item item) {
        LastNextBookingDates dates = bookingRepository.getLastNextBookingDatesByItemId(
                item.getId(),
                LocalDateTime.now()
        );
        item.setLastBooking(dates.getLastBooking());
        item.setNextBooking(dates.getNextBooking());
    }

}
