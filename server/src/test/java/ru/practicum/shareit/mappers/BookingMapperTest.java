package ru.practicum.shareit.mappers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapperImpl;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingMapperTest {

    private final BookingMapperImpl bookingMapper;

    @Test
    public void bookingMapperTest() {

        User user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setEmail("ivan@server.ru");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Roman");
        user2.setEmail("roman@server.ru");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(user);
        comment.setText("some comment");

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setAuthor(user);
        comment2.setText("comment #2");

        Item item = new Item();
        item.setId(1L);
        item.setName("item name");
        item.setDescription("item description");
        item.setOwner(user.getId());
        item.setAvailable(true);
        item.setRequestId(2L);
        item.setComments(Arrays.asList(comment, comment2));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user2);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);

        Booking booking2 = new Booking();
        booking.setId(2L);
        booking.setItem(item);
        booking.setBooker(user2);
        booking.setStart(LocalDateTime.now().plusDays(3));
        booking.setEnd(LocalDateTime.now().plusDays(4));
        booking.setStatus(BookingStatus.APPROVED);


        BookingDto bookingDto = bookingMapper.bookingToDto(booking);

        assertThat(bookingDto.getId(), equalTo(booking.getId()));
        assertThat(bookingDto.getItem().getId(), equalTo(item.getId()));
        assertThat(bookingDto.getItem().getName(), equalTo(item.getName()));
        assertThat(bookingDto.getItem().getComments().size(), equalTo(2));
        assertThat(bookingDto.getBooker().getId(), equalTo(user2.getId()));
        assertThat(bookingDto.getBooker().getName(), equalTo(user2.getName()));
        assertThat(bookingDto.getBooker().getEmail(), equalTo(user2.getEmail()));

        Booking bookingFromDto = bookingMapper.dtoToBooking(bookingDto);

        assertThat(bookingFromDto.getId(), equalTo(booking.getId()));
        assertThat(bookingFromDto.getItem().getId(), equalTo(item.getId()));
        assertThat(bookingFromDto.getItem().getName(), equalTo(item.getName()));
        assertThat(bookingFromDto.getItem().getComments().size(), equalTo(2));
        assertThat(bookingFromDto.getBooker().getId(), equalTo(user2.getId()));
        assertThat(bookingFromDto.getBooker().getName(), equalTo(user2.getName()));
        assertThat(bookingFromDto.getBooker().getEmail(), equalTo(user2.getEmail()));

        NewBookingDto newBookingDto = new NewBookingDto();
        newBookingDto.setItemId(1L);
        newBookingDto.setStart(LocalDateTime.now().plusDays(3));
        newBookingDto.setEnd(LocalDateTime.now().plusDays(4));

        bookingFromDto = bookingMapper.newDtoToBooking(newBookingDto);
        assertThat(bookingFromDto.getStart(), equalTo(newBookingDto.getStart()));
        assertThat(bookingFromDto.getEnd(), equalTo(newBookingDto.getEnd()));

        List<Booking> bookingList = bookingMapper.dtoListToBookingList(
                Arrays.asList(bookingMapper.bookingToDto(booking), bookingMapper.bookingToDto(booking2))
        );
        assertThat(bookingList.size(), equalTo(2));
        assertThat(bookingList.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(bookingList.get(1).getStatus(), equalTo(booking2.getStatus()));

    }
}
