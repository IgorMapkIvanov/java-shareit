package ru.practicum.shareit.Item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.shareit.data.CommentAndCommentDtoData.comment1;
import static ru.practicum.shareit.data.CommentAndCommentDtoData.commentDto1;

class CommentMapperTest {

    @Test
    void shouldCommentToCommentDtoTest() {
        CommentDto commentDto = CommentMapper.toDto(comment1);

        assertNotNull(commentDto);
        assertEquals(1, commentDto.getId());
        assertEquals("comment1", commentDto.getText());
        assertEquals(comment1.getAuthorName().getName(), commentDto.getAuthorName());
        assertEquals(comment1.getCreated(), commentDto.getCreated());
    }

    @Test
    void shouldCommentDtoToCommentTest() {
        Comment comment = CommentMapper.fromDto(commentDto1);

        assertNotNull(comment);
        assertEquals(1, comment.getId());
        assertEquals("commentDto1Text", comment.getText());
    }
}