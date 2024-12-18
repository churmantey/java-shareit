package ru.practicum.shareit.booking;

import java.util.Optional;

public enum BookingSearchStates {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingSearchStates> from(String stringState) {
        for (BookingSearchStates state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }


}
