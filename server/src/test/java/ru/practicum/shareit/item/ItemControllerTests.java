package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.general.Headers.USER_ID_HEADER;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    private static final String API_PREFIX = "/items";

    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private ItemWithBookingsDto itemWithBookingsDto;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {

        userDto = new UserDto(
                1L,
                "John Doe",
                "john.doe@mail.com");

        itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("first item");
        itemDto1.setDescription("first description");
        itemDto1.setAvailable(true);
        itemDto1.setOwner(userDto.getId());

        itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("second item");
        itemDto2.setDescription("second description");
        itemDto2.setAvailable(true);
        itemDto2.setOwner(userDto.getId());

        itemWithBookingsDto = new ItemWithBookingsDto();
        itemWithBookingsDto.setId(1L);
        itemWithBookingsDto.setName("first item");
        itemWithBookingsDto.setDescription("first description");
        itemWithBookingsDto.setAvailable(true);
        itemWithBookingsDto.setOwner(userDto.getId());
        itemWithBookingsDto.setLastBooking(LocalDateTime.now().minusDays(1));
        itemWithBookingsDto.setNextBooking(LocalDateTime.now().plusDays(1));

    }

    @Test
    public void createItemTest() throws Exception {
        when(itemService.createItem(any(ItemDto.class)))
                .thenReturn(itemDto1);

        mvc.perform(post(API_PREFIX)
                        .content(mapper.writeValueAsString(itemDto1))
                        .header(USER_ID_HEADER, userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$.owner", is(userDto.getId()), Long.class));
    }

    @Test
    public void getItemTest() throws Exception {
        when(itemService.getItem(anyLong()))
                .thenReturn(itemWithBookingsDto);

        mvc.perform(get(API_PREFIX + "/" + itemWithBookingsDto.getId().toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithBookingsDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithBookingsDto.getName())))
                .andExpect(jsonPath("$.description", is(itemWithBookingsDto.getDescription())))
                .andExpect(jsonPath("$.lastBooking", is(itemWithBookingsDto.getLastBooking().format(dtf))))
                .andExpect(jsonPath("$.nextBooking", is(itemWithBookingsDto.getNextBooking().format(dtf))));
    }

    @Test
    public void getItemsByOwnerTest() {

    }

    @Test
    public void updateItemTest() {

    }

    @Test
    public void findItemsByTextTest() {

    }

    @Test
    public void addCommentTest() {

    }

}
