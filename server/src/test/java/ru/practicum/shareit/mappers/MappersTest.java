package ru.practicum.shareit.mappers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingMapperImpl;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapperImpl;
import ru.practicum.shareit.item.dto.ItemMapperImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapperImpl;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MappersTest {

    private final UserMapperImpl userMapper;
    private final ItemMapperImpl itemMapper;
    private final BookingMapperImpl bookingMapper;
    private final CommentMapperImpl commentMapper;

    @Test
    public void userMapperTests() {
        User user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setEmail("ivan@server.ru");

        User user2 = new User();
        user.setId(2L);
        user.setName("Roman");
        user.setEmail("roman@server.ru");

        UserDto userDto = userMapper.userToDto(user);
        assertThat(userDto.getId(), equalTo(user.getId()));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));

        List<UserDto> userDtoList = userMapper.userListToDtoList(Arrays.asList(user, user2));

        assertThat(userDtoList.size(), equalTo(2));
        assertThat(userDtoList.get(0).getId(), equalTo(user.getId()));
        assertThat(userDtoList.get(0).getName(), equalTo(user.getName()));
        assertThat(userDtoList.get(1).getId(), equalTo(user2.getId()));
        assertThat(userDtoList.get(1).getName(), equalTo(user2.getName()));

        UserDto userDto1 = new UserDto();
        userDto1.setId(3L);
        userDto1.setName("Samuel Jackson");
        userDto1.setEmail("jackson@samuel.com");

        UserDto userDto2 = new UserDto();
        userDto2.setId(4L);
        userDto2.setName("John Travolta");
        userDto2.setEmail("john@travolta.com");

        User userFromDto = userMapper.dtoToUser(userDto1);
        assertThat(userDto1.getId(), equalTo(userFromDto.getId()));
        assertThat(userDto1.getName(), equalTo(userFromDto.getName()));
        assertThat(userDto1.getEmail(), equalTo(userFromDto.getEmail()));

        List<User> userList = userMapper.dtoListToUserList(Arrays.asList(userDto1, userDto2));
        assertThat(userList.size(), equalTo(2));
        assertThat(userList.get(0).getId(), equalTo(userDto1.getId()));
        assertThat(userList.get(0).getName(), equalTo(userDto1.getName()));
        assertThat(userList.get(1).getId(), equalTo(userDto2.getId()));
        assertThat(userList.get(1).getName(), equalTo(userDto2.getName()));

    }

    @Test
    public void commentMapperTests() {

        User user = new User();
        user.setName("Роман");
        user.setEmail("yandex@mail.com");

        Item item = new Item();
        item.setName("item");
        item.setDescription("useful item");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setText("some comment");

        CommentDto commentDto = commentMapper.mapToCommentDto(comment);
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setAuthor(user);
        comment2.setText("comment #2");

        List<CommentDto> commentDtoList = commentMapper.mapToCommentDtoList(Arrays.asList(comment, comment2));

        assertThat(commentDtoList.size(), equalTo(2));
        assertThat(commentDtoList.get(0).getId(), equalTo(comment.getId()));
        assertThat(commentDtoList.get(0).getText(), equalTo(comment.getText()));
        assertThat(commentDtoList.get(1).getId(), equalTo(comment2.getId()));
        assertThat(commentDtoList.get(1).getText(), equalTo(comment2.getText()));

    }

}
