package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.general.Headers.USER_ID_HEADER;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                    @Valid @RequestBody NewBookingDto newBookingDto) {
        log.info("CREATE booking {}, booker {}", newBookingDto, userId);
        return bookingService.createBooking(newBookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        log.info("CONFIRM booking {}, user {}, approved {}", bookingId, userId, approved);
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    // GET /bookings/{bookingId}
    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @PathVariable Long bookingId) {
        log.info("GET booking {}, user {}", bookingId, userId);
        return bookingService.getBooking(bookingId, userId);
    }

    // GET /bookings?state={state}
    @GetMapping
    public List<BookingDto> getUserBookingsByState(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "all") String state) {
        log.info("GET user {} bookings, state = {}", userId, state);
        return bookingService.getBookingsByUser(userId, getStateFromString(state));
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookingsByState(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "all") String state) {
        log.info("GET owner {} bookings, state = {}", userId, state);
        return bookingService.getBookingsByItemOwner(userId, getStateFromString(state));
    }

    private BookingSearchStates getStateFromString(String state) {
        return switch (state.toLowerCase()) {
            case "current" -> BookingSearchStates.CURRENT;
            case "past" -> BookingSearchStates.PAST;
            case "future" -> BookingSearchStates.FUTURE;
            case "waiting" -> BookingSearchStates.WAITING;
            case "rejected" -> BookingSearchStates.REJECTED;
            default -> BookingSearchStates.ALL;
        };
    }
}
