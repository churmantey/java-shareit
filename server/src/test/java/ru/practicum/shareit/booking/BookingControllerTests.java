package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.general.Headers.USER_ID_HEADER;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {

    private static final String API_PREFIX = "/bookings";

    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;
    private BookingDto bookingDto2;
    private NewBookingDto newBookingDto;
    private UserDto userDto;
    private UserDto userDto2;
    private ItemDto itemDto1;
    private ItemDto itemDto2;

    @BeforeEach
    public void setUp() {

        userDto = new UserDto(
                1L,
                "John Doe",
                "john.doe@mail.com");

        userDto2 = new UserDto(
                2L,
                "Don Joe",
                "don.joe@mail.com");

        itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("first item");
        itemDto1.setDescription("first description");
        itemDto1.setAvailable(true);
        itemDto1.setOwner(userDto2.getId());

        itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("second item");
        itemDto2.setDescription("second description");
        itemDto2.setAvailable(true);
        itemDto2.setOwner(userDto2.getId());

        newBookingDto = new NewBookingDto();
        newBookingDto.setItemId(itemDto1.getId());
        newBookingDto.setStart(LocalDateTime.now().plusDays(1));
        newBookingDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItem(itemDto1);
        bookingDto.setBooker(userDto);
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingDto2 = new BookingDto();
        bookingDto2.setId(1L);
        bookingDto2.setItem(itemDto2);
        bookingDto2.setBooker(userDto);
        bookingDto2.setStatus(BookingStatus.WAITING);
        bookingDto2.setStart(LocalDateTime.now().plusHours(6));
        bookingDto2.setEnd(LocalDateTime.now().plusDays(7));
    }

    @Test
    public void createBookingTest() throws Exception {
        when(bookingService.createBooking(any(NewBookingDto.class), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post(API_PREFIX)
                        .content(mapper.writeValueAsString(newBookingDto))
                        .header(USER_ID_HEADER, userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(dtf))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(dtf))));
    }

    @Test
    public void confirmBookingTest() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch(API_PREFIX + "/" + bookingDto.getId() + "?approved=true")
                        .header(USER_ID_HEADER, userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(dtf))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(dtf))));
    }

    @Test
    public void getBookingTest() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get(API_PREFIX + "/" + bookingDto.getId())
                        .header(USER_ID_HEADER, userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(dtf))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(dtf))));
    }

    @Test
    public void getUserBookingsByStateTest() throws Exception {
        when(bookingService.getBookingsByUser(anyLong(), any(BookingSearchStates.class)))
                .thenReturn(Arrays.asList(bookingDto, bookingDto2));

        mvc.perform(get(API_PREFIX + "?state=waiting")
                        .header(USER_ID_HEADER, userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(List.class)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().format(dtf))))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().format(dtf))))
                .andExpect(jsonPath("$[1].id", is(bookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].item.id", is(bookingDto2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[1].booker.id", is(bookingDto2.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus().toString())))
                .andExpect(jsonPath("$[1].start", is(bookingDto2.getStart().format(dtf))))
                .andExpect(jsonPath("$[1].end", is(bookingDto2.getEnd().format(dtf))));

    }

    @Test
    public void getOwnerBookingsByStateTest() throws Exception {
        when(bookingService.getBookingsByItemOwner(anyLong(), any(BookingSearchStates.class)))
                .thenReturn(Arrays.asList(bookingDto, bookingDto2));

        mvc.perform(get(API_PREFIX + "/owner?state=all")
                        .header(USER_ID_HEADER, userDto2.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(List.class)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().format(dtf))))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().format(dtf))))
                .andExpect(jsonPath("$[1].id", is(bookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].item.id", is(bookingDto2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[1].booker.id", is(bookingDto2.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus().toString())))
                .andExpect(jsonPath("$[1].start", is(bookingDto2.getStart().format(dtf))))
                .andExpect(jsonPath("$[1].end", is(bookingDto2.getEnd().format(dtf))));

    }

}
