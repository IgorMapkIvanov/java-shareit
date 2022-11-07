package ru.practicum.shareit.data;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

import static ru.practicum.shareit.data.ItemAndItemDtoData.item1;
import static ru.practicum.shareit.data.UserAndUserDtoData.user2;
import static ru.practicum.shareit.data.UserAndUserDtoData.user3;

public class CommentAndCommentDtoData {
    public static Comment comment1 = Comment.builder()
            .id(1L)
            .text("comment1")
            .created(LocalDateTime.now())
            .authorName(user3)
            .item(item1)
            .build();

    public static Comment comment2 = Comment.builder()
            .id(2L)
            .text("comment2")
            .created(LocalDateTime.now())
            .authorName(user2)
            .item(item1)
            .build();

    public static CommentDto commentDto1 = CommentDto.builder()
            .id(1L)
            .text("commentDto1Text")
            .build();
}
