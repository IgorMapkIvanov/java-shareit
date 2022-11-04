package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplIntegrationTest {
    private final EntityManager em;
    private final RequestServiceImpl requestService;
    private User user1;
    private User user2;

    @BeforeEach
    void beforeEach() {
        user1 = new User();
        user1.setName("name");
        user1.setEmail("name@email.ru");
        em.persist(user1);

        user2 = new User();
        user2.setName("name2");
        user2.setEmail("name2@email.ru");
        em.persist(user2);

    }

    @AfterEach
    void afterEach() {
        em.createNativeQuery("truncate table users");
        em.createNativeQuery("truncate table items");
        em.createNativeQuery("truncate table bookings");
        em.createNativeQuery("truncate table requests");
    }

    @Test
    void shouldAddRequestIsOkTest() {
        RequestDto request = RequestDto.builder()
                .description("newDescription")
                .build();

        RequestDto requestDto = requestService.addRequest(user1.getId(), request);
        TypedQuery<Request> query = em.createQuery("SELECT rt from Request rt where rt.id = :id", Request.class);
        Request request1 = query.setParameter("id", requestDto.getId()).getSingleResult();

        assertEquals(requestDto.getDescription(), request1.getDescription());
        assertEquals(requestDto.getId(), request1.getId());
        assertEquals(requestDto.getCreated(), request1.getCreated());
    }

    @Test
    void shouldAddRequestWhenUserIdIsWrongTest() {
        RequestDto request = RequestDto.builder()
                .description("newDescription")
                .build();

        NotFoundException a = assertThrows(NotFoundException.class, () -> requestService.addRequest(15L, request));
        assertEquals(a.getMessage(), "Пользователь с ID = 15 не найден.");
    }

    @Test
    void shouldGetUserRequestsWhenIsOkTest() {
        Request request = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(2))
                .description("newDescription1")
                .build();
        em.persist(request);

        Request request1 = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(1))
                .description("newDescription2")
                .build();
        em.persist(request1);

        List<RequestDto> requests = requestService.getUserRequests(user1.getId(), null);
        TypedQuery<Request> query =
                em.createQuery("SELECT rt from Request rt where rt.requester.id = :id", Request.class);
        List<Request> requestsBase = query.setParameter("id", user1.getId()).getResultList();

        assertEquals(requests.size(), 2);
        assertEquals(requests.get(0).getDescription(), requestsBase.get(0).getDescription());
        assertEquals(requests.get(0).getId(), requestsBase.get(0).getId());
        assertEquals(requests.get(0).getCreated(), requestsBase.get(0).getCreated());
        assertEquals(requests.get(1).getDescription(), requestsBase.get(1).getDescription());
        assertEquals(requests.get(1).getId(), requestsBase.get(1).getId());
        assertEquals(requests.get(1).getCreated(), requestsBase.get(1).getCreated());
    }

    @Test
    void shouldGetUserRequestsWhenUserIdIsWrongTest() {
        Request request = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(2))
                .description("newDescription1")
                .build();
        em.persist(request);

        NotFoundException a = assertThrows(NotFoundException.class,
                () -> requestService.getUserRequests(15L, null));
        assertEquals(a.getMessage(), "Пользователь с ID = 15 не найден.");
    }

    @Test
    void shouldGetAllRequestsWhenIsOkWithoutPagingTest() {
        Request request = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(2))
                .description("newDescription1")
                .build();
        em.persist(request);

        Request request1 = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(1))
                .description("newDescription2")
                .build();
        em.persist(request1);

        List<RequestDto> requests = requestService.getAllRequests(user2.getId(), null);
        TypedQuery<Request> query =
                em.createQuery("SELECT rt from Request rt ", Request.class);
        List<Request> requestsBase = query.getResultList();

        assertEquals(requests.size(), 2);
        assertEquals(requests.get(0).getDescription(), requestsBase.get(0).getDescription());
        assertEquals(requests.get(0).getId(), requestsBase.get(0).getId());
        assertEquals(requests.get(0).getCreated(), requestsBase.get(0).getCreated());
        assertEquals(requests.get(1).getDescription(), requestsBase.get(1).getDescription());
        assertEquals(requests.get(1).getId(), requestsBase.get(1).getId());
        assertEquals(requests.get(1).getCreated(), requestsBase.get(1).getCreated());
    }

    @Test
    void shouldGetAllRequestsWhenIsOkWithPagingTest() {
        Request request = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(2))
                .description("newDescription1")
                .build();
        em.persist(request);

        Request request1 = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(1))
                .description("newDescription2")
                .build();
        em.persist(request1);

        List<RequestDto> requests = requestService
                .getAllRequests(user2.getId(), PageRequest.of(0, 1));
        TypedQuery<Request> query =
                em.createQuery("SELECT rt from Request rt where rt.id = :id", Request.class);
        List<Request> requestsBase = query.setParameter("id", request.getId()).getResultList();

        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getDescription(), requestsBase.get(0).getDescription());
        assertEquals(requests.get(0).getId(), requestsBase.get(0).getId());
        assertEquals(requests.get(0).getCreated(), requestsBase.get(0).getCreated());
    }

    @Test
    void shouldGetRequestByIdIsOkTest() {
        Request request = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(2))
                .description("newDescription1")
                .build();
        em.persist(request);

        RequestDto requestMethod = requestService.getRequestById(user1.getId(), request.getId());
        TypedQuery<Request> query = em.createQuery("SELECT rt from Request rt where rt.id = :id", Request.class);
        Request requestBase = query.setParameter("id", request.getId()).getSingleResult();

        assertEquals(requestMethod.getDescription(), requestBase.getDescription());
        assertEquals(requestMethod.getId(), requestBase.getId());
        assertEquals(requestMethod.getCreated(), requestBase.getCreated());
    }

    @Test
    void shouldGetRequestByIdIsOktWithItemsTest() {
        Request request = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(2))
                .description("newDescription1")
                .build();
        em.persist(request);

        Item item = Item.builder()
                .name("item")
                .description("description")
                .requestId(request.getId())
                .available(true)
                .owner(user2)
                .build();
        em.persist(item);

        RequestDto requestMethod = requestService.getRequestById(user1.getId(), request.getId());
        TypedQuery<Request> query = em.createQuery("SELECT rt from Request rt where rt.id = :id", Request.class);
        Request requestBase = query.setParameter("id", request.getId()).getSingleResult();

        assertEquals(requestMethod.getDescription(), requestBase.getDescription());
        assertEquals(requestMethod.getId(), requestBase.getId());
        assertEquals(requestMethod.getCreated(), requestBase.getCreated());
        assertEquals(requestMethod.getItems().size(), 1);
        assertEquals(requestMethod.getItems().get(0).getName(), item.getName());
        assertEquals(requestMethod.getItems().get(0).getDescription(), item.getDescription());
        assertEquals(requestMethod.getItems().get(0).getAvailable(), item.getAvailable());
    }

    @Test
    void shouldGetRequestByIdWhenUserIdIsWrongTest() {
        Request request = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(2))
                .description("newDescription")
                .build();
        em.persist(request);

        NotFoundException a =
                assertThrows(NotFoundException.class, () -> requestService.getRequestById(15L, request.getId()));
        assertEquals(a.getMessage(), "Пользователь с ID = 15 не найден.");
    }

    @Test
    void shouldGetRequestByIdWhenRequestIdIsWrongTest() {
        Request request = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(2))
                .description("newDescription")
                .build();
        em.persist(request);

        NotFoundException a =
                assertThrows(NotFoundException.class, () -> requestService.getRequestById(user1.getId(), 15L));
        assertEquals(a.getMessage(), "Запрос с ID = 15 не найден.");
    }
}