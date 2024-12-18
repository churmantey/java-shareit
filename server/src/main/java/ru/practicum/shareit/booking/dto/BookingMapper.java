package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.Booking;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking dtoToBooking(BookingDto bookingDto);

    Booking newDtoToBooking(NewBookingDto bookingDto);

    BookingDto bookingToDto(Booking booking);

    List<Booking> dtoListToBookingList(List<BookingDto> bookingDto);

    List<BookingDto> bookingListToDtoList(List<Booking> bookingDto);

}
