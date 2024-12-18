package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoJsonTests {

    private final JacksonTester<ItemRequestDto> json;

    private final LocalDateTime now = LocalDateTime.now();
    private ItemRequestDto requestDto;

    @BeforeEach
    public void setUp() {

        ItemForRequestDto itemDto1 = new ItemForRequestDto();
        ItemForRequestDto itemDto2 = new ItemForRequestDto();

        itemDto1.setId(1L);
        itemDto1.setName("item #1");
        itemDto2.setId(2L);
        itemDto2.setName("item #2");

        requestDto = new ItemRequestDto();

        requestDto.setId(1L);
        requestDto.setDescription("request description");
        requestDto.setAuthor(2L);
        requestDto.setCreated(now);
        requestDto.setItems(Arrays.asList(itemDto1, itemDto2));
    }

    @Test
    void testItemRequestDto() throws Exception {

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("request description");
        assertThat(result).extractingJsonPathNumberValue("$.author").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(2);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("item #1");
        assertThat(result).extractingJsonPathNumberValue("$.items[1].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.items[1].name").isEqualTo("item #2");
    }
}