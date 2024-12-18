package ru.practicum.shareit.mappers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapperImpl;
import ru.practicum.shareit.user.User;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentMapperTest {

    private final CommentMapperImpl commentMapper;

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
