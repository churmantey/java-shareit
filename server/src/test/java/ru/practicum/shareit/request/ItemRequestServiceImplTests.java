package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTests {

    private final EntityManager em;
    private final ItemRequestService service;
    private final UserService userService;

    private UserDto userDto1;
    private UserDto userDto2;
    private UserDto userDto3;
    private NewItemRequestDto newRequestDto1;
    private NewItemRequestDto newRequestDto2;

    @BeforeEach
    public void setUp() {
        userDto1 = new UserDto(1L, "Пётр Иванов", "some@email.com");
        userDto2 = new UserDto(2L, "Иван Петров", "some.other@email.com");
        userDto3 = new UserDto(3L, "Сергей Андреев", "serge@email.com");

        userDto1 = userService.createUser(userDto1);
        userDto2 = userService.createUser(userDto2);
        userDto3 = userService.createUser(userDto3);

        newRequestDto1 = new NewItemRequestDto();
        newRequestDto1.setDescription("first request description");

        newRequestDto2 = new NewItemRequestDto();
        newRequestDto2.setDescription("second request description");
    }

    @Test
    public void getOtherItemRequestsTest() {
        ItemRequestDto requestDto1 = service.createItemRequest(userDto1.getId(), newRequestDto1);
        ItemRequestDto requestDto2 = service.createItemRequest(userDto2.getId(), newRequestDto2);

        List<ItemRequestDto> requests = service.getOtherItemRequests(userDto3.getId());

        assertThat(requests, notNullValue());
        assertThat(requests.size(), equalTo(2));
        assertThat(requests.get(0).getDescription(), equalTo(requestDto1.getDescription()));
        assertThat(requests.get(0).getAuthor(), equalTo(userDto1.getId()));
        assertThat(requests.get(0).getCreated(), lessThanOrEqualTo(LocalDateTime.now()));

        assertThat(requests.get(1).getDescription(), equalTo(requestDto2.getDescription()));
        assertThat(requests.get(1).getAuthor(), equalTo(userDto2.getId()));
        assertThat(requests.get(1).getCreated(), lessThanOrEqualTo(LocalDateTime.now()));
        assertThat(requests.get(0).getCreated(), lessThanOrEqualTo(requests.get(1).getCreated()));
    }

    @Test
    public void createItemRequestTest() {
        ItemRequestDto requestDto1 = service.createItemRequest(userDto1.getId(), newRequestDto1);
        assertThat(requestDto1.getDescription(), equalTo(newRequestDto1.getDescription()));
    }

    @Test
    public void getItemRequestTest() {
        ItemRequestDto requestDto1 = service.createItemRequest(userDto1.getId(), newRequestDto1);

        ItemRequestDto retrievedRequest = service.getItemRequest(requestDto1.getId());

        assertThat(requestDto1.getId(), equalTo(retrievedRequest.getId()));
        assertThat(requestDto1.getCreated(), equalTo(retrievedRequest.getCreated()));
        assertThat(requestDto1.getDescription(), equalTo(retrievedRequest.getDescription()));
    }

    @Test
    public void getUserItemRequestsTest() {
        ItemRequestDto requestDto1 = service.createItemRequest(userDto1.getId(), newRequestDto1);
        ItemRequestDto requestDto2 = service.createItemRequest(userDto1.getId(), newRequestDto2);

        List<ItemRequestDto> requests = service.getUserItemRequests(userDto1.getId());

        assertThat(requests, notNullValue());
        assertThat(requests.size(), equalTo(2));
        assertThat(requests.get(0).getDescription(), equalTo(requestDto1.getDescription()));
        assertThat(requests.get(0).getAuthor(), equalTo(userDto1.getId()));
        assertThat(requests.get(0).getCreated(), lessThanOrEqualTo(LocalDateTime.now()));

        assertThat(requests.get(1).getDescription(), equalTo(requestDto2.getDescription()));
        assertThat(requests.get(1).getAuthor(), equalTo(userDto1.getId()));
        assertThat(requests.get(1).getCreated(), lessThanOrEqualTo(LocalDateTime.now()));
        assertThat(requests.get(0).getCreated(), lessThanOrEqualTo(requests.get(1).getCreated()));
    }

    @Test
    public void deleteRequestTest() {
        ItemRequestDto requestDto1 = service.createItemRequest(userDto1.getId(), newRequestDto1);
        service.deleteRequest(requestDto1.getId());
        Assertions.assertThrows(NotFoundException.class, () -> {service.getItemRequest(requestDto1.getId());});
    }

}
