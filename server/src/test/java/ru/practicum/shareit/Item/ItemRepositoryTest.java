package ru.practicum.shareit.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.shareit.data.ItemAndItemDtoData.item1;
import static ru.practicum.shareit.data.ItemAndItemDtoData.item2;
import static ru.practicum.shareit.data.UserAndUserDtoData.user1;
import static ru.practicum.shareit.data.UserAndUserDtoData.user2;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    private PageRequest pageRequest;
    private static final String SEARCH_TEXT = "Description";

    @BeforeEach
    void setUp() {
        userRepository.save(user1);
        userRepository.save(user2);

        itemRepository.save(item1);
        itemRepository.save(item2);

        pageRequest = PageRequest.of(0, 10);

    }

    @Test
    void searchItemsByNameOrDescriptionContainingTextIgnoreCaseAndAvailable() {
        List<Item> items = itemRepository
                .searchItemsByNameOrDescriptionContainingTextIgnoreCaseAndAvailable(
                        SEARCH_TEXT, true, pageRequest);

        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals(1L, items.stream().findFirst().get().getId());
    }
}