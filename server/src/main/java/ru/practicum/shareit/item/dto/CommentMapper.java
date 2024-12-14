package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "author", target = "authorName", qualifiedByName = "userToAuthorName")
    CommentDto mapToCommentDto(Comment comment);

    List<CommentDto> mapToCommentDtoList(List<Comment> commentList);

    @Named("userToAuthorName")
    static String userToAuthor(User author) {
        return author.getName();
    }
}
