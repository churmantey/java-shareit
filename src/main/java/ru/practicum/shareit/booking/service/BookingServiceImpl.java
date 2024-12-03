package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingSearchStates;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;
    private final BookingJpaRepository bookingRepository;
    private final ItemJpaRepository itemRepository;
    private final UserJpaRepository userRepository;

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking not found, id = " + bookingId));
        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().equals(userId))
            throw new AccessException("User with id = " + userId +
                    " has no access to booking with id = " + bookingId);
        return bookingMapper.bookingToDto(booking);
    }

    @Override
    @Transactional
    public BookingDto createBooking(NewBookingDto newBookingDto, Long bookerId) {
        Booking booking = bookingMapper.newDtoToBooking(newBookingDto);
        booking.setBooker(validateUserById(bookerId));
        booking.setItem(validateItemById(newBookingDto.getItemId()));
        validateBooking(booking);
        return bookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long bookingId, Long userId, Boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found, id = " + bookingId));
        if (!booking.getItem().getOwner().equals(userId))
            throw new AccessException("User with id = " + userId +
                    " has no access to item with id = " + booking.getItem().getId());
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.bookingToDto(booking);
        //return bookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, BookingSearchStates searchState) {
        validateUserById(userId);
        List<Booking> bookingList = switch (searchState) {
            case CURRENT -> bookingRepository.findApprovedCurrentUserBookings(userId, LocalDateTime.now());
            case PAST -> bookingRepository.findApprovedPastUserBookings(userId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findApprovedFutureUserBookings(userId, LocalDateTime.now());
            case WAITING, REJECTED ->
                    bookingRepository.findByBookerIdAndStatusEqualsOrderByStart(userId, searchState.toString());
            default -> bookingRepository.findByBookerIdOrderByStart(userId);
        };
        return bookingMapper.bookingListToDtoList(bookingList);
    }

    @Override
    public List<BookingDto> getBookingsByItemOwner(Long userId, BookingSearchStates searchState) {
        validateUserById(userId);
        List<Booking> bookingList = switch (searchState) {
            case CURRENT -> bookingRepository.findCurrentByItemOwner(userId, LocalDateTime.now());
            case PAST -> bookingRepository.findPastByItemOwner(userId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findFutureByItemOwner(userId, LocalDateTime.now());
            case WAITING, REJECTED ->
                    bookingRepository.findByItemOwnerAndStatusEqualsOrderByStart(userId, searchState.toString());
            default -> bookingRepository.findByItemOwnerOrderByStart(userId);
        };

        return bookingMapper.bookingListToDtoList(bookingList);
    }

    private Item validateItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found, id = " + itemId));
        if (!item.getAvailable()) {
            throw new ValidationException("Item with id = " + itemId + " is not available");
        }
        return item;
    }

    private User validateUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found, id = " + userId));
    }

    private void validateBooking(Booking booking) {
        if (booking == null) throw new ValidationException("Booking of null received");
        if (!booking.getStart().isBefore(booking.getEnd()))
            throw new ValidationException("Start date is before end date");
        if (booking.getStart().equals(booking.getEnd()))
            throw new ValidationException("Zero booking duration");
        if (booking.getItem().getOwner().equals(booking.getBooker().getId()))
            throw new ValidationException("Item cannot be booked by owner");
    }
}
