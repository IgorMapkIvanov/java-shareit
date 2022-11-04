package ru.practicum.shareit.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.shareit.data.CommentAndCommentDtoData.comment1;
import static ru.practicum.shareit.data.CommentAndCommentDtoData.comment2;
import static ru.practicum.shareit.data.ItemAndItemDtoData.item1;
import static ru.practicum.shareit.data.UserAndUserDtoData.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        itemRepository.save(item1);

        commentRepository.save(comment1);
        commentRepository.save(comment2);
    }

    @Test
    void shouldGetCommentForItemWithId() {
        List<CommentDto> list = commentRepository.getComments(1L);

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(1L, list.stream().findFirst().get().getId());
    }
}