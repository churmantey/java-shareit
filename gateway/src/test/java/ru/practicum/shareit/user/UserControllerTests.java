package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {

    private static final String API_PREFIX = "/users";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserClient userClient;

    private UserDto userDto;
    private UserDto userDto2;
    private UserDto updatedDto;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto(
                1L,
                "John Doe",
                "john.doe@mail.com");

        userDto2 = new UserDto(
                2L,
                "Don Joe",
                "donjoe@mail.com");

        updatedDto = new UserDto();
        updatedDto.setName("Roger Waters");
        updatedDto.setEmail("dark.side@moon.com");
    }

    @Test
    public void saveNewUser() throws Exception {
        when(userClient.createUser(any()))
                .thenReturn(ResponseEntity.ok(userDto));

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

        when(userClient.updateUser(any()))
                .thenReturn(ResponseEntity.ok(
                        new UserDto(userDto.getId(), updatedDto.getName(), updatedDto.getEmail())
                ));

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

    @Test
    public void getUserTest() throws Exception {
        when(userClient.getUser(anyLong()))
                .thenReturn(ResponseEntity.ok(userDto));

        mvc.perform(get(API_PREFIX + "/" + userDto.getId().toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void getAllUsersTest() throws Exception {

        when(userClient.getAllUsers())
                .thenReturn(ResponseEntity.ok(Arrays.asList(userDto, userDto2)));

        mvc.perform(get(API_PREFIX)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(List.class)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail())));

    }

    @Test
    public void deleteUserTest() throws Exception {

        doNothing().when(userClient).deleteUserById(anyLong());

        mvc.perform(delete(API_PREFIX + "/" + userDto.getId().intValue())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).deleteUserById(anyLong());
    }

}