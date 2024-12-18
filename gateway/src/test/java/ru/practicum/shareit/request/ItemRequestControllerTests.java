package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Headers.USER_ID_HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {

    private static final String API_PREFIX = "/requests";

    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestClient requestClient;

    private UserDto userDto;
    private UserDto userDto2;
    private ItemRequestDto requestDto;
    private ItemRequestDto requestDto2;
    private NewItemRequestDto newRequestDto;

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

        requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("item request description");
        requestDto.setAuthor(userDto.getId());
        requestDto.setCreated(LocalDateTime.now().minusDays(1));

        requestDto2 = new ItemRequestDto();
        requestDto2.setId(2L);
        requestDto2.setDescription("item request #2 description");
        requestDto2.setAuthor(userDto.getId());
        requestDto2.setCreated(LocalDateTime.now());

        newRequestDto = new NewItemRequestDto();
        newRequestDto.setDescription("new item request description");
    }

    @Test
    public void createItemRequestTest() throws Exception {
        when(requestClient.createItemRequest(anyLong(), any(NewItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(requestDto));

        mvc.perform(post(API_PREFIX)
                        .content(mapper.writeValueAsString(newRequestDto))
                        .header(USER_ID_HEADER, userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.author", is(requestDto.getAuthor()), Long.class))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().format(dtf))));

    }

    @Test
    public void getUserItemRequestsTest() throws Exception {
        when(requestClient.getUserItemRequests(anyLong()))
                .thenReturn(ResponseEntity.ok(Arrays.asList(requestDto, requestDto2)));

        mvc.perform(get(API_PREFIX)
                        .header(USER_ID_HEADER, userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(List.class)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$[0].author", is(requestDto.getAuthor()), Long.class))
                .andExpect(jsonPath("$[0].created", is(requestDto.getCreated().format(dtf))))
                .andExpect(jsonPath("$[1].id", is(requestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(requestDto2.getDescription())))
                .andExpect(jsonPath("$[1].author", is(requestDto2.getAuthor()), Long.class))
                .andExpect(jsonPath("$[1].created", is(requestDto2.getCreated().format(dtf))));

    }

    @Test
    public void getOtherItemRequestsTest() throws Exception {
        when(requestClient.getOtherItemRequests(anyLong()))
                .thenReturn(ResponseEntity.ok(Arrays.asList(requestDto, requestDto2)));

        mvc.perform(get(API_PREFIX + "/all")
                        .header(USER_ID_HEADER, userDto2.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(List.class)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$[0].author", is(requestDto.getAuthor()), Long.class))
                .andExpect(jsonPath("$[0].created", is(requestDto.getCreated().format(dtf))))
                .andExpect(jsonPath("$[1].id", is(requestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(requestDto2.getDescription())))
                .andExpect(jsonPath("$[1].author", is(requestDto2.getAuthor()), Long.class))
                .andExpect(jsonPath("$[1].created", is(requestDto2.getCreated().format(dtf))));

    }

    @Test
    public void getItemRequestTest() throws Exception {
        when(requestClient.getItemRequest(anyLong()))
                .thenReturn(ResponseEntity.ok(requestDto));

        mvc.perform(get(API_PREFIX + "/" + requestDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.author", is(requestDto.getAuthor()), Long.class))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().format(dtf))));

    }

}
