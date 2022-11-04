package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select new ru.practicum.shareit.item.dto.CommentDto(c.id, c.text, c.authorName.name, c.created)" +
            "from Comment as c " +
            "where (c.item.id = ?1)")
    List<CommentDto> getComments(Long id);

}