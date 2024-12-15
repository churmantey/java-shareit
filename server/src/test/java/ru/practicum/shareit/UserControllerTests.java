package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.general.Headers.USER_ID_HEADER;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {

//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private UserController controller;

    private static final String API_PREFIX = "/users";

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;
    private UserDto updatedDto;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto(
                1L,
                "John Doe",
                "john.doe@mail.com");

        updatedDto = new UserDto();
        updatedDto.setName("Roger Waters");
        updatedDto.setEmail("dark.side@moon.com");
    }

    @Test
    public void saveNewUser() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(userDto);

        mvc.perform(post(API_PREFIX)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void updateUser() throws Exception {

        when(userService.updateUser(any()))
                .thenReturn(new UserDto(userDto.getId(), updatedDto.getName(), updatedDto.getEmail()));

        mvc.perform(patch(API_PREFIX + "/" + userDto.getId().toString())
                    .content(mapper.writeValueAsString(updatedDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedDto.getName())))
                .andExpect(jsonPath("$.email", is(updatedDto.getEmail())));

    }

}
