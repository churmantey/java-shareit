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
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTests {

    private final EntityManager em;
    private final BookingService service;
    private final ItemService itemService;
    private final UserService userService;

    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private UserDto userDto1;
    private UserDto userDto2;
    private UserDto userDto3;
    private NewBookingDto newBookingDto1;
    private NewBookingDto newBookingDto2;

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
    }

    @Test
    public void getBookingsByItemOwnerTest() {
        BookingDto booking1 = service.createBooking(newBookingDto1, userDto2.getId());
        BookingDto booking2 = service.createBooking(newBookingDto2, userDto3.getId());

        booking1 = service.approveBooking(booking1.getId(), userDto1.getId(), true);
        booking2 = service.approveBooking(booking2.getId(), userDto1.getId(), true);

        List<BookingDto> bookings = service.getBookingsByItemOwner(userDto1.getId(), BookingSearchStates.ALL);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getItem().getId(), equalTo(itemDto1.getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(userDto2.getId()));
        assertThat(bookings.get(0).getStart(), equalTo(newBookingDto1.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(newBookingDto1.getEnd()));
        assertThat(bookings.get(1).getItem().getId(), equalTo(itemDto2.getId()));
        assertThat(bookings.get(1).getBooker().getId(), equalTo(userDto3.getId()));
        assertThat(bookings.get(1).getStart(), equalTo(newBookingDto2.getStart()));
        assertThat(bookings.get(1).getEnd(), equalTo(newBookingDto2.getEnd()));

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

    }

}
