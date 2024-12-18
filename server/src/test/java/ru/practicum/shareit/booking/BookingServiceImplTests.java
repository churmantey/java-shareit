package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTests {

    private final EntityManager em;
    private final BookingServiceImpl service;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;

    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private UserDto userDto1;
    private UserDto userDto2;
    private UserDto userDto3;
    private NewBookingDto newBookingDto1;
    private NewBookingDto newBookingDto2;
    private NewBookingDto newBookingDto3;

    @BeforeEach
    public void setUp() {
        userDto1 = new UserDto(1L, "Пётр Иванов", "some@email.com");
        userDto2 = new UserDto(2L, "Иван Петров", "some.other@email.com");
        userDto3 = new UserDto(3L, "Сергей Андреев", "serge@email.com");

        userDto1 = userService.createUser(userDto1);
        userDto2 = userService.createUser(userDto2);
        userDto3 = userService.createUser(userDto3);

        itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("first item");
        itemDto1.setDescription("first description");
        itemDto1.setAvailable(true);
        itemDto1.setOwner(userDto1.getId());

        itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("second item");
        itemDto2.setDescription("second description");
        itemDto2.setAvailable(true);
        itemDto2.setOwner(userDto1.getId());

        itemDto1 = itemService.createItem(itemDto1);
        itemDto2 = itemService.createItem(itemDto2);

        newBookingDto1 = new NewBookingDto();
        newBookingDto1.setItemId(itemDto1.getId());
        newBookingDto1.setStart(LocalDateTime.now().minusDays(2));
        newBookingDto1.setEnd(LocalDateTime.now().minusDays(1));

        newBookingDto2 = new NewBookingDto();
        newBookingDto2.setItemId(itemDto2.getId());
        newBookingDto2.setStart(LocalDateTime.now().plusHours(1));
        newBookingDto2.setEnd(LocalDateTime.now().plusDays(1));

        newBookingDto3 = new NewBookingDto();
        newBookingDto3.setItemId(itemDto2.getId());
        newBookingDto3.setStart(LocalDateTime.now().minusHours(1));
        newBookingDto3.setEnd(LocalDateTime.now().plusDays(1));

    }

    @Test
    public void createBookingTest() {
        BookingDto booking1 = service.createBooking(newBookingDto1, userDto2.getId());
        assertThat(booking1, notNullValue());
        assertThat(booking1.getItem().getId(), equalTo(itemDto1.getId()));
        assertThat(booking1.getBooker().getId(), equalTo(userDto2.getId()));
        assertThat(booking1.getStart(), equalTo(newBookingDto1.getStart()));
        assertThat(booking1.getEnd(), equalTo(newBookingDto1.getEnd()));
        assertThat(booking1.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    public void createBookingUnavailableItemTest() {
        itemDto1.setAvailable(false);
        itemService.updateItem(itemDto1);
        assertThrows(ValidationException.class, () -> service.createBooking(newBookingDto1, userDto2.getId()));
    }

    @Test
    public void createBookingByOwnerTest() {
        assertThrows(ValidationException.class, () -> service.createBooking(newBookingDto1, itemDto1.getOwner()));
    }

    @Test
    public void createInvalidBookingTest() {
        newBookingDto1.setStart(newBookingDto1.getEnd());
        assertThrows(ValidationException.class, () -> service.createBooking(newBookingDto1, userDto2.getId()));

        newBookingDto2.setEnd(newBookingDto2.getStart().minusHours(1));
        assertThrows(ValidationException.class, () -> service.createBooking(newBookingDto2, userDto2.getId()));

    }

    @Test
    public void getBookingTest() {
        BookingDto booking1 = service.createBooking(newBookingDto1, userDto2.getId());

        BookingDto storedBooking = service.getBooking(booking1.getId(), userDto2.getId());
        assertThat(booking1, notNullValue());
        assertThat(storedBooking.getItem().getId(), equalTo(itemDto1.getId()));
        assertThat(storedBooking.getBooker().getId(), equalTo(userDto2.getId()));
        assertThat(storedBooking.getStart(), equalTo(newBookingDto1.getStart()));
        assertThat(storedBooking.getEnd(), equalTo(newBookingDto1.getEnd()));
        assertThat(storedBooking.getStatus(), equalTo(BookingStatus.WAITING));

        assertThrows(NotFoundException.class,
                () -> service.getBooking(booking1.getId() + 1, userDto2.getId()));

        assertThrows(AccessException.class,
                () -> service.getBooking(booking1.getId(), userDto2.getId() + 1));

    }


    @Test
    public void approveBookingTest() {
        BookingDto booking1 = service.createBooking(newBookingDto1, userDto2.getId());
        assertThat(booking1, notNullValue());
        assertThat(booking1.getItem().getId(), equalTo(itemDto1.getId()));
        assertThat(booking1.getBooker().getId(), equalTo(userDto2.getId()));
        assertThat(booking1.getStart(), equalTo(newBookingDto1.getStart()));
        assertThat(booking1.getEnd(), equalTo(newBookingDto1.getEnd()));
        assertThat(booking1.getStatus(), equalTo(BookingStatus.WAITING));
        BookingDto approvedBooking = service.approveBooking(booking1.getId(), userDto1.getId(), true);
        assertThat(approvedBooking.getId(), equalTo(booking1.getId()));
        assertThat(approvedBooking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    public void getBookingsByUserTest() {
        BookingDto booking1 = service.createBooking(newBookingDto1, userDto2.getId());
        BookingDto booking2 = service.createBooking(newBookingDto2, userDto2.getId());
        BookingDto booking3 = service.createBooking(newBookingDto3, userDto2.getId());

        List<BookingDto> bookings = service.getBookingsByUser(userDto2.getId(), BookingSearchStates.WAITING);
        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(3));

        bookings = service.getBookingsByUser(userDto2.getId(), BookingSearchStates.ALL);
        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(3));

        service.approveBooking(booking1.getId(), userDto1.getId(), true);
        service.approveBooking(booking2.getId(), userDto1.getId(), true);
        service.approveBooking(booking3.getId(), userDto1.getId(), true);

        bookings = service.getBookingsByUser(userDto2.getId(), BookingSearchStates.CURRENT);
        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking3.getId()));

        bookings = service.getBookingsByUser(userDto2.getId(), BookingSearchStates.PAST);
        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking1.getId()));

        bookings = service.getBookingsByUser(userDto2.getId(), BookingSearchStates.FUTURE);
        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking2.getId()));

        service.approveBooking(booking2.getId(), userDto1.getId(), false);
        bookings = service.getBookingsByUser(userDto2.getId(), BookingSearchStates.REJECTED);
        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking2.getId()));

    }

    @Test
    public void getBookingsByItemOwnerTest() {
        BookingDto booking1 = service.createBooking(newBookingDto1, userDto2.getId());
        BookingDto booking2 = service.createBooking(newBookingDto2, userDto3.getId());
        BookingDto booking3 = service.createBooking(newBookingDto3, userDto3.getId());

        List<BookingDto> bookings = service.getBookingsByItemOwner(userDto1.getId(), BookingSearchStates.WAITING);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(3));
        assertThat(bookings.get(0).getItem().getId(), equalTo(itemDto1.getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(userDto2.getId()));
        assertThat(bookings.get(0).getStart(), equalTo(newBookingDto1.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(newBookingDto1.getEnd()));
        assertThat(bookings.get(1).getItem().getId(), equalTo(itemDto2.getId()));
        assertThat(bookings.get(1).getBooker().getId(), equalTo(userDto3.getId()));
        assertThat(bookings.get(1).getStart(), equalTo(newBookingDto3.getStart()));
        assertThat(bookings.get(1).getEnd(), equalTo(newBookingDto3.getEnd()));

        bookings = service.getBookingsByItemOwner(userDto1.getId(), BookingSearchStates.ALL);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(3));
        assertThat(bookings.get(0).getItem().getId(), equalTo(itemDto1.getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(userDto2.getId()));
        assertThat(bookings.get(0).getStart(), equalTo(newBookingDto1.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(newBookingDto1.getEnd()));
        assertThat(bookings.get(1).getItem().getId(), equalTo(itemDto2.getId()));
        assertThat(bookings.get(1).getBooker().getId(), equalTo(userDto3.getId()));
        assertThat(bookings.get(1).getStart(), equalTo(newBookingDto3.getStart()));
        assertThat(bookings.get(1).getEnd(), equalTo(newBookingDto3.getEnd()));

        service.approveBooking(booking1.getId(), userDto1.getId(), true);
        service.approveBooking(booking2.getId(), userDto1.getId(), true);
        service.approveBooking(booking3.getId(), userDto1.getId(), true);

        bookings = service.getBookingsByItemOwner(userDto1.getId(), BookingSearchStates.PAST);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getItem().getId(), equalTo(itemDto1.getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(userDto2.getId()));
        assertThat(bookings.get(0).getStart(), equalTo(newBookingDto1.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(newBookingDto1.getEnd()));

        bookings = service.getBookingsByItemOwner(userDto1.getId(), BookingSearchStates.FUTURE);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getItem().getId(), equalTo(itemDto2.getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(userDto3.getId()));
        assertThat(bookings.get(0).getStart(), equalTo(newBookingDto2.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(newBookingDto2.getEnd()));

        bookings = service.getBookingsByItemOwner(userDto1.getId(), BookingSearchStates.CURRENT);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getItem().getId(), equalTo(itemDto2.getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(userDto3.getId()));
        assertThat(bookings.get(0).getStart(), equalTo(newBookingDto3.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(newBookingDto3.getEnd()));

        service.approveBooking(booking3.getId(), userDto1.getId(), false);

        bookings = service.getBookingsByItemOwner(userDto1.getId(), BookingSearchStates.REJECTED);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getItem().getId(), equalTo(itemDto2.getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(userDto3.getId()));
        assertThat(bookings.get(0).getStart(), equalTo(newBookingDto3.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(newBookingDto3.getEnd()));

    }
}
