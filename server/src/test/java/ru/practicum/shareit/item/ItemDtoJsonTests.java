package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTests {

    private final JacksonTester<ItemDto> json;
    private final LocalDateTime now = LocalDateTime.now();
    private ItemDto itemDto;

    @BeforeEach
    public void setUp() {

        itemDto = new ItemDto();

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
    }

    @Test
    void testItemDto() throws Exception {

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item name");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("item description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(222);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(3);
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(2);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo("first comment");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo("first author");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo(now.minusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        assertThat(result).extractingJsonPathNumberValue("$.comments[1].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.comments[1].text")
                .isEqualTo("second comment");
        assertThat(result).extractingJsonPathStringValue("$.comments[1].authorName")
                .isEqualTo("second author");
        assertThat(result).extractingJsonPathStringValue("$.comments[1].created")
                .isEqualTo(now.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}