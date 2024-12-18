package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTests {

    private final JacksonTester<BookingDto> json;
    private final LocalDateTime now = LocalDateTime.now();

    private BookingDto bookingDto;

    @BeforeEach
    public void setUp() {

        UserDto userDto = new UserDto(
                1L,
                "John Doe",
                "john.doe@mail.com"
        );

        ItemDto itemDto = new ItemDto();

        itemDto.setId(1L);
        itemDto.setName("item name");
        itemDto.setDescription("item description");
        itemDto.setAvailable(true);
        itemDto.setOwner(222L);
        itemDto.setRequestId(3L);

        CommentDto comment1 = new CommentDto();
        comment1.setId(1L);
        comment1.setText("first comment");
        comment1.setAuthorName("first author");
        comment1.setCreated(now.minusDays(2));

        CommentDto comment2 = new CommentDto();
        comment2.setId(2L);
        comment2.setText("second comment");
        comment2.setAuthorName("second author");
        comment2.setCreated(now.minusDays(1));

        itemDto.setComments(Arrays.asList(comment1, comment2));

        bookingDto = new BookingDto();

        bookingDto.setId(1L);
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));
        bookingDto.setItem(itemDto);
        bookingDto.setBooker(userDto);
        bookingDto.setStatus(BookingStatus.APPROVED);
    }

    @Test
    void testBookingDto() throws Exception {

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(now.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(now.plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(BookingStatus.APPROVED.toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item name");
        assertThat(result).extractingJsonPathStringValue("$.item.description")
                .isEqualTo("item description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.owner").isEqualTo(222);
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(3);
        assertThat(result).extractingJsonPathArrayValue("$.item.comments").hasSize(2);
        assertThat(result).extractingJsonPathNumberValue("$.item.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.comments[0].text")
                .isEqualTo("first comment");
        assertThat(result).extractingJsonPathStringValue("$.item.comments[0].authorName")
                .isEqualTo("first author");
        assertThat(result).extractingJsonPathStringValue("$.item.comments[0].created")
                .isEqualTo(now.minusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        assertThat(result).extractingJsonPathNumberValue("$.item.comments[1].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.comments[1].text")
                .isEqualTo("second comment");
        assertThat(result).extractingJsonPathStringValue("$.item.comments[1].authorName")
                .isEqualTo("second author");
        assertThat(result).extractingJsonPathStringValue("$.item.comments[1].created")
                .isEqualTo(now.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("john.doe@mail.com");

    }
}