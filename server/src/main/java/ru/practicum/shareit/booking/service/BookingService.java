package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingSearchStates;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.util.List;

public interface BookingService {

    BookingDto getBooking(Long bookingId, Long userId);

    BookingDto createBooking(NewBookingDto newBookingDto, Long bookerId);

    BookingDto approveBooking(Long bookingId, Long userId, Boolean isApproved);

    List<BookingDto> getBookingsByUser(Long userId, BookingSearchStates searchState);

    List<BookingDto> getBookingsByItemOwner(Long userId, BookingSearchStates searchState);

}
