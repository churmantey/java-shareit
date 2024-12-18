package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTests {

    private final EntityManager em;
    private final UserServiceImpl service;
    private UserDto userDto;
    private UserDto userDto2;
    private UserDto updatedDto;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto(1L, "Пётр Иванов", "some@email.com");
        userDto2 = new UserDto(2L, "Иван Петров", "some.other@email.com");
        updatedDto = new UserDto();
        updatedDto.setId(1L);
        updatedDto.setName("Roger Waters");
        updatedDto.setEmail("dark.side@moon.com");
    }

    // Интеграционные тесты
    @Test
    public void createUserTest() {
        service.createUser(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));

        userDto.setId(userDto.getId() + 1);
        assertThrows(DuplicateException.class, () -> service.createUser(userDto));
    }

    @Test
    public void updateUserTest() {
        UserDto createdUser = service.createUser(userDto);
        updatedDto.setId(createdUser.getId());
        service.updateUser(updatedDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", updatedDto.getId())
                .getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(updatedDto.getName()));
        assertThat(user.getEmail(), equalTo(updatedDto.getEmail()));
    }

    @Test
    public void getUserTest() {
        userDto = service.createUser(userDto);

        UserDto storedUserDto = service.getUser(userDto.getId());

        assertThat(storedUserDto, notNullValue());
        assertThat(storedUserDto.getName(), equalTo(userDto.getName()));
        assertThat(storedUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void getNonexistentUserTest() {
        assertThrows(NotFoundException.class, () -> service.getUser(userDto.getId()));
    }

    @Test
    public void getAllUsersTest() {
        userDto = service.createUser(userDto);
        userDto2 = service.createUser(userDto2);

        List<UserDto> userList = service.getAllUsers();

        assertThat(userList, notNullValue());
        assertThat(userList.size(), equalTo(2));
    }

    @Test
    public void deleteUserTest() {
        userDto = service.createUser(userDto);
        Assertions.assertDoesNotThrow(() -> service.deleteUserById(userDto.getId()));
        Assertions.assertThrows(NotFoundException.class, () -> service.deleteUserById(userDto.getId()));
    }

}
