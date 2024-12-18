package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.general.LastNextBookingDates;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemJpaRepository itemRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final UserJpaRepository userRepository;
    private final BookingJpaRepository bookingRepository;
    private final CommentJpaRepository commentRepository;
    private final ItemRequestJpaRepository requestRepository;

    @Override
    public ItemWithBookingsDto getItem(Long itemId) {
        Item item = getItemById(itemId);
        fillComments(item);
        return itemMapper.itemToDtoWithBookings(item);
    }


    @Override
    public List<ItemWithBookingsDto> getItemsByOwner(Long userId) {
        User user = getUserById(userId);
        return itemMapper.itemListToDtoListWithBookings(
                itemRepository.findAllByOwnerOrderByIdAsc(user.getId())
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
        return itemMapper.itemToDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto updatedItemDto) {
        Item item = getItemById(updatedItemDto.getId());
        if (!item.getOwner().equals(updatedItemDto.getOwner())) {
            throw new AccessException("User with id = " + updatedItemDto.getOwner() +
                    " can't update item with id = " + updatedItemDto.getId());
        }
        if (updatedItemDto.getName() != null
                && !updatedItemDto.getName().isBlank()) item.setName(updatedItemDto.getName());
        if (updatedItemDto.getDescription() != null
                && !updatedItemDto.getDescription().isBlank()) item.setDescription(updatedItemDto.getDescription());
        if (updatedItemDto.getAvailable() != null) item.setAvailable(updatedItemDto.getAvailable());
        if (updatedItemDto.getRequestId() != null) item.setRequestId(updatedItemDto.getRequestId());
        validateItem(item);
        return itemMapper.itemToDto(item);
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

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, NewCommentDto newCommentDto) {
        User user = getUserById(userId);
        Item item = getItemById(itemId);
        Optional<Booking> maybeBooking = bookingRepository.findFirstByBookerIdAndItemIdAndStatusEqualsAndEndBefore(
                userId,
                itemId,
                BookingStatus.APPROVED,
                LocalDateTime.now());
        if (maybeBooking.isPresent()) {
            Comment comment = new Comment();
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setText(newCommentDto.getText());
            comment.setCreated(LocalDateTime.now());
            commentRepository.save(comment);
            return commentMapper.mapToCommentDto(comment);
        } else {
            throw new ValidationException("User " + userId + " has not used item " + itemId + " yet");
        }
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found, " + itemId));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found, " + userId));
    }

    private void validateItem(Item item) {
        if (item == null) throw new ValidationException("Item of null received");
        if (item.getName() == null
                || item.getName().isBlank()) throw new ValidationException("Item name cannot be empty");
        if (item.getDescription() == null
                || item.getDescription().isBlank()) throw new ValidationException("Item description cannot be empty");
        if (item.getAvailable() == null) throw new ValidationException("Item without available field received");
        if (item.getRequestId() != null
                && !requestRepository.existsById(item.getRequestId())) {
            throw new ValidationException("Item request not found, id = " + item.getRequestId());
        }
        if (item.getOwner() != null) {
            User owner = getUserById(item.getOwner());
            if (owner == null) {
                throw new NotFoundException("Item owner with id = " + item.getOwner() + " not found");
            }
        }
    }

    private void fillComments(Item item) {
        item.setComments(
                commentRepository.findByItemIdOrderByCreated(item.getId())
        );
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
