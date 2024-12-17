package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.service.ItemService;
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
    private ItemWithBookingsDto itemWithBookingsDto2;
    private UserDto userDto;
    private CommentDto commentDto;
    private NewCommentDto newCommentDto;

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
        itemWithBookingsDto.setName("first item with bookings");
        itemWithBookingsDto.setDescription("first item description");
        itemWithBookingsDto.setAvailable(true);
        itemWithBookingsDto.setOwner(userDto.getId());
        itemWithBookingsDto.setLastBooking(LocalDateTime.now().minusDays(1));
        itemWithBookingsDto.setNextBooking(LocalDateTime.now().plusDays(1));

        itemWithBookingsDto2 = new ItemWithBookingsDto();
        itemWithBookingsDto2.setId(2L);
        itemWithBookingsDto2.setName("second item with bookings");
        itemWithBookingsDto2.setDescription("second item description");
        itemWithBookingsDto2.setAvailable(true);
        itemWithBookingsDto2.setOwner(userDto.getId());
        itemWithBookingsDto2.setLastBooking(LocalDateTime.now().minusDays(1));
        itemWithBookingsDto2.setNextBooking(LocalDateTime.now().plusDays(1));

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("comment text");
        commentDto.setCreated(LocalDateTime.now());
        commentDto.setAuthorName("Author name");

        newCommentDto = new NewCommentDto();
        newCommentDto.setText("new comment text");
        newCommentDto.setAuthorName("new comment author");

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
    public void getItemsByOwnerTest() throws Exception {

        when(itemService.getItemsByOwner(anyLong()))
                .thenReturn(Arrays.asList(itemWithBookingsDto, itemWithBookingsDto2));

        mvc.perform(get(API_PREFIX)
                        .header(USER_ID_HEADER, userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(List.class)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemWithBookingsDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemWithBookingsDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemWithBookingsDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemWithBookingsDto.getAvailable())))
                .andExpect(jsonPath("$[0].owner", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking", is(itemWithBookingsDto.getLastBooking().format(dtf))))
                .andExpect(jsonPath("$[0].nextBooking", is(itemWithBookingsDto.getNextBooking().format(dtf))))
                .andExpect(jsonPath("$[1].id", is(itemWithBookingsDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemWithBookingsDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemWithBookingsDto2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemWithBookingsDto2.getAvailable())))
                .andExpect(jsonPath("$[1].owner", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[1].lastBooking", is(itemWithBookingsDto2.getLastBooking().format(dtf))))
                .andExpect(jsonPath("$[1].nextBooking", is(itemWithBookingsDto2.getNextBooking().format(dtf))));

    }

    @Test
    public void updateItemTest() throws Exception {
        when(itemService.updateItem(any(ItemDto.class)))
                .thenReturn(itemDto1);

        mvc.perform(patch(API_PREFIX + "/" + itemDto1.getId())
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
    public void findItemsByTextTest() throws Exception {
        when(itemService.findAvailableItemsByText(anyString()))
                .thenReturn(Arrays.asList(itemDto1, itemDto2));

        mvc.perform(get(API_PREFIX + "/search?text=" + anyString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(List.class)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$[0].owner", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDto2.getAvailable())))
                .andExpect(jsonPath("$[1].owner", is(userDto.getId()), Long.class));

    }

    @Test
    public void addCommentTest() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(NewCommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post(API_PREFIX + "/" + itemDto1.getId() + "/comment")
                        .content(mapper.writeValueAsString(newCommentDto))
                        .header(USER_ID_HEADER, userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().format(dtf))))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));

    }

}
