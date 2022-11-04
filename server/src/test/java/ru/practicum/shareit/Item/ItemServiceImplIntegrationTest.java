package ru.practicum.shareit.Item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegrationTest {
    private final EntityManager em;

    private final ItemServiceImpl service;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .name("name")
                .email("name@email.ru")
                .build();

        em.persist(user);
    }

    @AfterEach
    void afterEach() {
        em.createNativeQuery("truncate table users");
        em.createNativeQuery("truncate table items");
        em.createNativeQuery("truncate table bookings");
    }

    @Test
    void shouldUpdateItemTest() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        itemDto = service.addItemForUserWithId(itemDto, user.getId());

        ItemDto updateItemDto = ItemDto.builder()
                .id(itemDto.getId())
                .name("newName")
                .description("newDescription")
                .available(true)
                .build();

        updateItemDto = service.updateItemForUserWithId(updateItemDto, user.getId());

        TypedQuery<Item> query = em.createQuery("SELECT i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertEquals(item.getId(), updateItemDto.getId());
        assertEquals(item.getName(), updateItemDto.getName());
        assertEquals(item.getOwner(), user);
        assertEquals(item.getDescription(), updateItemDto.getDescription());
        assertEquals(item.getAvailable(), updateItemDto.getAvailable());

    }

    @Test
    void shouldAddItemForUserWithIdTest() {
        ItemDto itemDto = ItemDto.builder()
                .name("It.name")
                .description("description")
                .available(true)
                .build();

        itemDto = service.addItemForUserWithId(itemDto, user.getId());

        TypedQuery<Item> query = em.createQuery("SELECT i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getOwner(), user);
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void shouldAddItemForUserWithMissingIdInDbTest() {
        ItemDto itemDto = ItemDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();
        Long idNotInDb = 15L;

        assertThrows(NotFoundException.class, () -> service.addItemForUserWithId(itemDto, idNotInDb));
    }

    @Test
    void shouldFindItemByIdForOwnerWithoutBookingTest() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        itemDto = service.addItemForUserWithId(itemDto, user.getId());
        ItemDtoWithBooking result = service.getItemById(user.getId(), itemDto.getId());

        TypedQuery<Item> query = em.createQuery("SELECT i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getOwner(), user);
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getDescription(), result.getDescription());
        assertNull(result.getNextBooking());
        assertNull(result.getLastBooking());
        assertEquals(List.of(), result.getComments());
    }

    @Test
    void shouldFindItemByIdForNewUserWithoutBookingTest() {
        User newUser = User.builder()
                .name("newUser")
                .email("newUser@email.ru")
                .build();
        em.persist(newUser);
        ItemDto itemDto = ItemDto.builder()
                .name("It.name")
                .description("description")
                .available(true)
                .build();
        itemDto = service.addItemForUserWithId(itemDto, user.getId());

        ItemDtoWithBooking result = service.getItemById(newUser.getId(), itemDto.getId());

        TypedQuery<Item> query = em.createQuery("SELECT i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getOwner(), user);
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertNull(result.getNextBooking());
        assertNull(result.getLastBooking());
        assertEquals(List.of(), result.getComments());
    }

    @Test
    void shouldGetItemByIdWhenUserIdNotInDbTest() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        Long userIdNotInDb = 15L;

        itemDto = service.addItemForUserWithId(itemDto, user.getId());

        ItemDto finalItemDto = itemDto;
        assertThrows(NotFoundException.class, () -> service.getItemById(userIdNotInDb, finalItemDto.getId()));
    }

    @Test
    void shouldDetItemByIdWhenItemIdNotInDbTest() {
        Long itemIdNotInDb = 15L;

        assertThrows(NotFoundException.class, () -> service.getItemById(user.getId(), itemIdNotInDb));
    }

    @Test
    void shouldUpdateItemNotInDbTest() {
        ItemDto itemDto = ItemDto.builder()
                .name("It.name")
                .description("description")
                .available(true)
                .build();
        itemDto = service.addItemForUserWithId(itemDto, user.getId());
        ItemDto updateItemDto = ItemDto.builder()
                .id(itemDto.getId())
                .name("newName")
                .description("dds")
                .available(true)
                .build();
        Long userIdNotInDb = 15L;

        assertThrows(NotFoundException.class, () -> service.updateItemForUserWithId(updateItemDto, userIdNotInDb));
    }

    @Test
    void shouldUpdateItemNotOwnerTest() {
        User user2 = new User();
        user2.setName("name2");
        user2.setEmail("name2@email.ru");
        em.persist(user2);
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        itemDto = service.addItemForUserWithId(itemDto, user.getId());

        ItemDto updateItemDto = ItemDto.builder()
                .id(itemDto.getId())
                .name("newName")
                .description("newDescription")
                .available(true)
                .build();

        assertThrows(NotFoundException.class, () -> service.updateItemForUserWithId(updateItemDto, user2.getId()));
    }

    @Test
    void shouldUpdateItemOnlyDescriptionTest() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        itemDto = service.addItemForUserWithId(itemDto, user.getId());

        ItemDto updateItemDto = ItemDto.builder()
                .id(itemDto.getId())
                .description("newDescription")
                .available(true)
                .build();
        updateItemDto = service.updateItemForUserWithId(updateItemDto, user.getId());

        TypedQuery<Item> query = em.createQuery("SELECT i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertEquals(item.getId(), updateItemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getOwner(), user);
        assertEquals(item.getDescription(), updateItemDto.getDescription());
        assertEquals(item.getAvailable(), updateItemDto.getAvailable());
    }

    @Test
    void shouldUpdateItemOnlyAvailableTest() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        itemDto = service.addItemForUserWithId(itemDto, user.getId());

        ItemDto updateItemDto = ItemDto.builder()
                .id(itemDto.getId())
                .description("description")
                .available(false)
                .build();
        updateItemDto = service.updateItemForUserWithId(updateItemDto, user.getId());

        TypedQuery<Item> query = em.createQuery("SELECT i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertEquals(item.getId(), updateItemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getOwner(), user);
        assertEquals(item.getDescription(), updateItemDto.getDescription());
        assertEquals(item.getAvailable(), updateItemDto.getAvailable());
    }

    @Test
    void shouldUpdateItemOnlyNameTest() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        itemDto = service.addItemForUserWithId(itemDto, user.getId());

        ItemDto updateItemDto = ItemDto.builder()
                .id(itemDto.getId())
                .name("newName")
                .available(true)
                .build();
        updateItemDto = service.updateItemForUserWithId(updateItemDto, user.getId());

        TypedQuery<Item> query = em.createQuery("SELECT i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertEquals(item.getId(), updateItemDto.getId());
        assertEquals(item.getName(), updateItemDto.getName());
        assertEquals(item.getOwner(), user);
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), updateItemDto.getAvailable());
    }

    @Test
    void shouldUpdateItemWhenAvailableIsEmptyInDtoTest() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        itemDto = service.addItemForUserWithId(itemDto, user.getId());

        ItemDto updateItemDto = ItemDto.builder()
                .id(itemDto.getId())
                .name("newName")
                .description("newDescription")
                .build();
        updateItemDto = service.updateItemForUserWithId(updateItemDto, user.getId());

        TypedQuery<Item> query = em.createQuery("SELECT i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertEquals(item.getId(), updateItemDto.getId());
        assertEquals(item.getName(), updateItemDto.getName());
        assertEquals(item.getOwner(), user);
        assertEquals(item.getDescription(), updateItemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void shouldFindAllUserItemsWithoutPageTest() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        ItemDto itemDto2 = ItemDto.builder()
                .name("name2")
                .description("description2")
                .available(true)
                .build();
        service.addItemForUserWithId(itemDto, user.getId());
        service.addItemForUserWithId(itemDto2, user.getId());

        List<ItemDtoWithBooking> items = service.getAllItemsForOwnerWithId(user.getId(), null);
        TypedQuery<Item> query = em.createQuery("SELECT i from Item i where i.owner.id = :id", Item.class);
        List<Item> itemsBase = query.setParameter("id", user.getId()).getResultList();

        assertEquals(items.size(), 2);
        assertEquals(items.get(0).getId(), itemsBase.get(0).getId());
        assertEquals(items.get(0).getName(), itemsBase.get(0).getName());
        assertEquals(items.get(0).getDescription(), itemsBase.get(0).getDescription());
        assertEquals(items.get(0).getAvailable(), itemsBase.get(0).getAvailable());
        assertEquals(items.get(1).getId(), itemsBase.get(1).getId());
        assertEquals(items.get(1).getName(), itemsBase.get(1).getName());
        assertEquals(items.get(1).getDescription(), itemsBase.get(1).getDescription());
        assertEquals(items.get(1).getAvailable(), itemsBase.get(1).getAvailable());
    }

    @Test
    void shouldFindAllUserItemsWithPageTest() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        ItemDto itemDto2 = ItemDto.builder()
                .name("name2")
                .description("description2")
                .available(true)
                .build();
        service.addItemForUserWithId(itemDto, user.getId());
        service.addItemForUserWithId(itemDto2, user.getId());

        List<ItemDtoWithBooking> items = service.getAllItemsForOwnerWithId(user.getId(), PageRequest.of(0, 10));
        TypedQuery<Item> query = em.createQuery("SELECT i from Item i where i.owner.id = :id", Item.class);
        List<Item> itemsBase = query.setParameter("id", user.getId()).getResultList();

        assertEquals(items.size(), 2);
        assertEquals(items.get(0).getId(), itemsBase.get(0).getId());
        assertEquals(items.get(0).getName(), itemsBase.get(0).getName());
        assertEquals(items.get(0).getDescription(), itemsBase.get(0).getDescription());
        assertEquals(items.get(0).getAvailable(), itemsBase.get(0).getAvailable());
        assertEquals(items.get(1).getId(), itemsBase.get(1).getId());
        assertEquals(items.get(1).getName(), itemsBase.get(1).getName());
        assertEquals(items.get(1).getDescription(), itemsBase.get(1).getDescription());
        assertEquals(items.get(1).getAvailable(), itemsBase.get(1).getAvailable());
    }

    @Test
    void addCommentCorrect() {
        Item item = Item.builder()
                .owner(user)
                .name("name")
                .description("description")
                .available(true)
                .build();
        em.persist(item);
        User user2 = new User();
        user2.setName("name2");
        user2.setEmail("name2@email.ru");
        em.persist(user2);

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        em.persist(booking);


        CommentDto commentDto = CommentDto.builder()
                .authorName("name2")
                .created(LocalDateTime.now().plusDays(3))
                .text("comment")
                .build();

        commentDto = service.addComment(user2.getId(), commentDto, item.getId());
        TypedQuery<Comment> query = em.createQuery("SELECT i from Comment i where i.id = :id", Comment.class);
        Comment commentInDb = query.setParameter("id", commentDto.getId()).getSingleResult();
        assertEquals(commentDto.getText(), commentInDb.getText());
        assertEquals(commentDto.getAuthorName(), commentInDb.getAuthorName().getName());
        assertEquals(commentDto.getCreated(), commentInDb.getCreated());

    }

    @Test
    void shouldAddCommentWhenUserIdNotInDbTest() {
        ItemDto itemDto = service.addItemForUserWithId(ItemDto.builder()
                        .name("name")
                        .description("description")
                        .available(true)
                        .build(),
                user.getId());
        Long userIdNotInDb = 15L;

        assertThrows(NotFoundException.class, () -> service.addComment(userIdNotInDb, new CommentDto(), itemDto.getId()));
    }

    @Test
    void shouldAddCommentWhenItemIdNotInDbTest() {
        Long itemIdNotInDb = 22L;

        assertThrows(NotFoundException.class, () -> service.addComment(user.getId(), new CommentDto(), itemIdNotInDb));
    }


    @Test
    void shouldAddCommentWhenNoBookingTest() {
        ItemDto itemDto = service.addItemForUserWithId(ItemDto.builder()
                        .name("name")
                        .description("description")
                        .available(true)
                        .build(),
                user.getId());

        User user2 = new User();
        user2.setName("name2");
        user2.setEmail("mail2@email.ru");
        em.persist(user2);

        CommentDto commentDto = CommentDto.builder()
                .authorName("Name")
                .created(LocalDateTime.now())
                .text("text")
                .build();

        assertThrows(BadRequestException.class, () -> service.addComment(user.getId(), commentDto, itemDto.getId()));
    }

    @Test
    void shouldSearchItemByTextInNameNoPageTest() {
        service.addItemForUserWithId(ItemDto.builder()
                        .name("name")
                        .description("description")
                        .available(true)
                        .build(),
                user.getId());
        service.addItemForUserWithId(ItemDto.builder()
                        .name("name2")
                        .description("description2")
                        .available(true)
                        .build(),
                user.getId());

        List<ItemDto> items = service.searchItemByText("name", null);
        TypedQuery<Item> query = em.createQuery("SELECT i from Item i", Item.class);
        List<Item> itemsBase = query.getResultList();

        assertEquals(items.size(), itemsBase.size());
        assertEquals(items.get(0).getId(), itemsBase.get(0).getId());
        assertEquals(items.get(0).getName(), itemsBase.get(0).getName());
        assertEquals(items.get(0).getDescription(), itemsBase.get(0).getDescription());
        assertEquals(items.get(0).getAvailable(), itemsBase.get(0).getAvailable());
        assertEquals(items.get(1).getId(), itemsBase.get(1).getId());
        assertEquals(items.get(1).getName(), itemsBase.get(1).getName());
        assertEquals(items.get(1).getDescription(), itemsBase.get(1).getDescription());
        assertEquals(items.get(1).getAvailable(), itemsBase.get(1).getAvailable());

    }

    @Test
    void shouldSearchItemByTextInDescriptionNoPageTest() {
        service.addItemForUserWithId(ItemDto.builder()
                        .name("name")
                        .description("description")
                        .available(true)
                        .build(),
                user.getId());
        service.addItemForUserWithId(ItemDto.builder()
                        .name("name2")
                        .description("description2")
                        .available(true)
                        .build(),
                user.getId());

        List<ItemDto> items = service.searchItemByText("description", null);
        TypedQuery<Item> query = em.createQuery("SELECT i from Item i", Item.class);
        List<Item> itemsBase = query.getResultList();

        assertEquals(items.size(), itemsBase.size());
        assertEquals(items.get(0).getId(), itemsBase.get(0).getId());
        assertEquals(items.get(0).getName(), itemsBase.get(0).getName());
        assertEquals(items.get(0).getDescription(), itemsBase.get(0).getDescription());
        assertEquals(items.get(0).getAvailable(), itemsBase.get(0).getAvailable());
        assertEquals(items.get(1).getId(), itemsBase.get(1).getId());
        assertEquals(items.get(1).getName(), itemsBase.get(1).getName());
        assertEquals(items.get(1).getDescription(), itemsBase.get(1).getDescription());
        assertEquals(items.get(1).getAvailable(), itemsBase.get(1).getAvailable());
    }

    @Test
    void shouldSearchItemByTextWhenBlankTest() {
        service.addItemForUserWithId(ItemDto.builder()
                        .name("name")
                        .description("description")
                        .available(true)
                        .build(),
                user.getId());
        service.addItemForUserWithId(ItemDto.builder()
                        .name("name2")
                        .description("description2")
                        .available(true)
                        .build(),
                user.getId());

        List<ItemDto> items = service.searchItemByText("", null);

        assertEquals(items.size(), 0);
    }

    @Test
    void shouldSearchItemByTextInDescriptionWithPageTest() {
        service.addItemForUserWithId(ItemDto.builder()
                        .name("name")
                        .description("description")
                        .available(true)
                        .build(),
                user.getId());
        service.addItemForUserWithId(ItemDto.builder()
                        .name("name2")
                        .description("description2")
                        .available(true)
                        .build(),
                user.getId());

        List<ItemDto> items = service.searchItemByText("description", null);
        TypedQuery<Item> query = em.createQuery("SELECT i from Item i", Item.class);
        List<Item> itemsBase = query.getResultList();

        assertEquals(items.size(), itemsBase.size());
        assertEquals(items.get(0).getId(), itemsBase.get(0).getId());
        assertEquals(items.get(0).getName(), itemsBase.get(0).getName());
        assertEquals(items.get(0).getDescription(), itemsBase.get(0).getDescription());
        assertEquals(items.get(0).getAvailable(), itemsBase.get(0).getAvailable());
        assertEquals(items.get(1).getId(), itemsBase.get(1).getId());
        assertEquals(items.get(1).getName(), itemsBase.get(1).getName());
        assertEquals(items.get(1).getDescription(), itemsBase.get(1).getDescription());
        assertEquals(items.get(1).getAvailable(), itemsBase.get(1).getAvailable());
    }
}