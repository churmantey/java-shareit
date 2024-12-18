package ru.practicum.shareit.general;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LastNextBookingDates {
    private final LocalDateTime lastBooking;
    private final LocalDateTime nextBooking;
}
