package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
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
class BookingServiceImplIntegrationTest {
    private final EntityManager em;

    private final BookingServiceImpl service;

    private User user1;

    private User user2;

    private Item item;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("name");
        user1.setEmail("name@email.ru");
        em.persist(user1);

        user2 = new User();
        user2.setName("name2");
        user2.setEmail("name2@email.ru");
        em.persist(user2);

        item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setOwner(user1);
        item.setAvailable(true);
        em.persist(item);
    }

    @AfterEach
    void tearDown() {
        em.createNativeQuery("truncate table users");
        em.createNativeQuery("truncate table items");
        em.createNativeQuery("truncate table bookings");
    }

    @Test
    void shouldAddBookingIsOkTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(item.getId())
                .build();
        bookingDto = service.addBooking(user2.getId(), bookingDto);

        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk where bk.booker.id = :id",
                Booking.class);
        Booking bookingDto1 = query.setParameter("id", bookingDto.getBooker().getId()).getSingleResult();

        assertEquals(bookingDto.getId(), bookingDto1.getId());
        assertEquals(bookingDto.getStart(), bookingDto1.getStart());
        assertEquals(bookingDto.getEnd(), bookingDto1.getEnd());
        assertEquals(bookingDto.getStatus(), bookingDto1.getStatus());
        assertEquals(bookingDto.getBooker(), bookingDto1.getBooker());
        assertEquals(bookingDto.getItem(), bookingDto1.getItem());
    }

    @Test
    void shouldAddBookingWhenUserIdIsNotDbTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(item.getId())
                .build();

        assertThrows(NotFoundException.class, () -> service.addBooking(15L, bookingDto));
    }

    @Test
    void shouldAddBookingWhenIsOwnerItemTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(item.getId())
                .build();

        assertThrows(NotFoundException.class, () -> service.addBooking(user1.getId(), bookingDto));

    }

    @Test
    void shouldAddBookingWhenItemIdIsNotInDbTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(15L)
                .build();

        NotFoundException exception =
                assertThrows(NotFoundException.class, () -> service.addBooking(user2.getId(), bookingDto));
        assertEquals(exception.getMessage(), "Вещь с ID = 15 не найдена.");

    }

    @Test
    void shouldApproveBookingWhenStatusIsApprovedTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.APPROVED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        assertThrows(BadRequestException.class, () -> service.approveBooking(user1.getId(), booking.getId(), true));
    }

    @Test
    void shouldApproveBookingWhenIsOkTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        ResponseBookingDto bookingDtoResponse = service.approveBooking(user1.getId(), booking.getId(), true);

        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk where bk.booker.id = :id", Booking.class);
        Booking bookingBase = query.setParameter("id", booking.getBooker().getId()).getSingleResult();

        assertEquals(bookingDtoResponse.getId(), bookingBase.getId());
        assertEquals(bookingDtoResponse.getStart(), bookingBase.getStart());
        assertEquals(bookingDtoResponse.getEnd(), bookingBase.getEnd());
        assertEquals(bookingDtoResponse.getStatus(), bookingBase.getStatus());
        assertEquals(bookingDtoResponse.getBooker().getId(), bookingBase.getBooker().getId());
        assertEquals(bookingDtoResponse.getItem().getId(), bookingBase.getItem().getId());
    }

    @Test
    void shouldAddBookingWhenItemAvailableIsFalseTest() {
        item.setAvailable(false);

        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(item.getId())
                .build();

        assertThrows(BadRequestException.class, () -> service.addBooking(user2.getId(), bookingDto));
    }

    @Test
    void shouldAddBookingWhenEndTimeBeforeStartTimeTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(item.getId())
                .build();

        assertThrows(BadRequestException.class, () -> service.addBooking(user2.getId(), bookingDto));
    }

    @Test
    void shouldApproveBookingWhenBookingIdIsNotInDbTest() {
        Long idIsNotInDb = 22L;

        assertThrows(NotFoundException.class, () -> service.approveBooking(user1.getId(), idIsNotInDb, true));
    }

    @Test
    void shouldApproveBookingWhenChangeNotYourBookingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        assertThrows(NotFoundException.class, () -> service.approveBooking(user2.getId(), booking.getId(), true));
    }

    @Test
    void shouldApproveBookingWhenSetRejectedIsOkTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        ResponseBookingDto bookingDtoResponse = service.approveBooking(user1.getId(), booking.getId(), false);
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk where bk.booker.id = :id", Booking.class);
        Booking bookingBase = query.setParameter("id", booking.getBooker().getId()).getSingleResult();

        assertEquals(bookingDtoResponse.getId(), bookingBase.getId());
        assertEquals(bookingDtoResponse.getStart(), bookingBase.getStart());
        assertEquals(bookingDtoResponse.getEnd(), bookingBase.getEnd());
        assertEquals(bookingDtoResponse.getStatus(), BookingStatus.REJECTED);
        assertEquals(bookingDtoResponse.getBooker().getId(), bookingBase.getBooker().getId());
        assertEquals(bookingDtoResponse.getItem().getId(), bookingBase.getItem().getId());
    }

    @Test
    void shouldGetBookingWhenIsOkTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.APPROVED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        ResponseBookingDto bookingDtoResponse = service.getBooking(user1.getId(), booking.getId());
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk where bk.id = :id", Booking.class);
        Booking bookingBase = query.setParameter("id", booking.getId()).getSingleResult();

        assertEquals(bookingDtoResponse.getId(), bookingBase.getId());
        assertEquals(bookingDtoResponse.getStart(), bookingBase.getStart());
        assertEquals(bookingDtoResponse.getEnd(), bookingBase.getEnd());
        assertEquals(bookingDtoResponse.getStatus(), bookingBase.getStatus());
        assertEquals(bookingDtoResponse.getBooker().getId(), bookingBase.getBooker().getId());
        assertEquals(bookingDtoResponse.getItem().getId(), bookingBase.getItem().getId());
    }

    @Test
    void shouldGetBookingWhenUserIdIsWrongTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.APPROVED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        User user = User.builder()
                .name("name3")
                .email("name3@male.ru")
                .build();
        em.persist(user);

        assertThrows(NotFoundException.class, () -> service.getBooking(user.getId(), booking.getId()));
    }

    @Test
    void shouldGetUserBookingsWhenIsOkWithoutPagingStateWaitingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);
        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service.getUserBookings(user2.getId(), "WAITING", null);
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.booker.id = :id and bk.status = :status", Booking.class);
        List<Booking> bookingInBase = query
                .setParameter("id", user2.getId())
                .setParameter("status", BookingStatus.WAITING)
                .getResultList();

        assertEquals(2, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingInBase.get(1).getId());
        assertEquals(bookings.get(0).getStart(), bookingInBase.get(1).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingInBase.get(1).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingInBase.get(1).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingInBase.get(1).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingInBase.get(1).getItem().getId());
        assertEquals(bookings.get(1).getId(), bookingInBase.get(0).getId());
        assertEquals(bookings.get(1).getStart(), bookingInBase.get(0).getStart());
        assertEquals(bookings.get(1).getEnd(), bookingInBase.get(0).getEnd());
        assertEquals(bookings.get(1).getStatus(), bookingInBase.get(0).getStatus());
        assertEquals(bookings.get(1).getBooker().getId(), bookingInBase.get(0).getBooker().getId());
        assertEquals(bookings.get(1).getItem().getId(), bookingInBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetUserBookingsWhenIsOkWithoutPagingStateAllTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service.getUserBookings(user2.getId(), "ALL", null);
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.booker.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", user2.getId())
                .getResultList();

        assertEquals(2, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(1).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(1).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(1).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(1).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(1).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(1).getItem().getId());
        assertEquals(bookings.get(1).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(1).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(1).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(1).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(1).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(1).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetBookingWhenBookingIdIsNotInDbTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.APPROVED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        assertThrows(NotFoundException.class, () -> service.getBooking(user1.getId(), 15L));
    }

    @Test
    void shouldGetBookingWhenUserIsNotBookerTest() {
        User user = new User();
        user.setName("name3");
        user.setEmail("name3@email.ru");
        em.persist(user);

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.APPROVED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        assertThrows(NotFoundException.class, () -> service.getBooking(user.getId(), booking.getId()));
    }

    @Test
    void shouldGetUserBookingsWhenIsOkWithoutPagingStateRejectedTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service.getUserBookings(user2.getId(), "REJECTED", null);
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.booker.id = :id and bk.status = :status", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", user2.getId())
                .setParameter("status", BookingStatus.REJECTED)
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetUserBookingsWhenIsOkWithoutPagingStateCurrentTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service.getUserBookings(user2.getId(), "CURRENT", null);
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", booking.getId())
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetUserBookingsWhenIsOkWithoutPagingStatePastTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(10))
                .end(LocalDateTime.now().minusHours(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service.getUserBookings(user2.getId(), "PAST", null);
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", booking.getId())
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());

    }

    @Test
    void shouldGetUserBookingsWhenIsOkWithoutPagingStateFutureTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(10))
                .end(LocalDateTime.now().minusHours(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service.getUserBookings(user2.getId(), "FUTURE", null);
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", booking2.getId())
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetUserBookingsWhenUserIdIsWrongTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(10))
                .end(LocalDateTime.now().minusHours(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);
        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                service.getUserBookings(15L, "FUTURE", null));
        assertEquals(exception.getMessage(), "Пользователь с ID = 15- не найден.");
    }

    @Test
    void shouldGetUserBookingsWhenStateIsWrongTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(10))
                .end(LocalDateTime.now().minusHours(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);
        String badState = "BAD_STATE";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                service.getUserBookings(user2.getId(), badState, null));
        assertEquals(exception.getMessage(), "No enum constant ru.practicum.shareit.booking.model.BookingState.BAD_STATE");
    }

    @Test
    void shouldGetUserBookingsWhenStateWaitingWithPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getUserBookings(user2.getId(), "WAITING", PageRequest.of(0, 1));
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.booker.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", user2.getId())
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(1).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(1).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(1).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(1).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(1).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(1).getItem().getId());

    }


    @Test
    void shouldGetUserBookingsWhenStateAllWithPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getUserBookings(user2.getId(), "ALL", PageRequest.of(0, 10));
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.booker.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", user2.getId())
                .getResultList();

        assertEquals(2, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(1).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(1).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(1).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(1).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(1).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(1).getItem().getId());
        assertEquals(bookings.get(1).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(1).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(1).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(1).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(1).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(1).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetUserBookingsWhenStateRejectedWithPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getUserBookings(user2.getId(), "REJECTED", PageRequest.of(0, 10));
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.booker.id = :id and bk.status = :status", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", user2.getId())
                .setParameter("status", BookingStatus.REJECTED)
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetUserBookingsWhenStateCurrentWithPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getUserBookings(user2.getId(), "CURRENT", PageRequest.of(0, 10));
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", booking.getId())
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetUserBookingsWhenStatePastWithPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(10))
                .end(LocalDateTime.now().minusHours(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service.getUserBookings(user2.getId(), "PAST", PageRequest.of(0, 10));
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", booking.getId())
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetUserBookingsWhenStateFutureWithPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(10))
                .end(LocalDateTime.now().minusHours(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service.getUserBookings(user2.getId(), "FUTURE", PageRequest.of(0, 10));
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", booking2.getId())
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetOwnerBookingsWhenStateWaitingWithoutPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service.getOwnerBookings(user1.getId(), "WAITING", null);
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.booker.id = :id and bk.status = :status", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", user2.getId())
                .setParameter("status", BookingStatus.WAITING)
                .getResultList();

        assertEquals(2, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(1).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(1).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(1).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(1).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(1).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(1).getItem().getId());
        assertEquals(bookings.get(1).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(1).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(1).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(1).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(1).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(1).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetOwnerBookingsWhenStateWaitingWithoutPagingForOneItemTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Item item1 = Item.builder()
                .owner(user2)
                .name("testItem")
                .available(true)
                .description("coolItem")
                .build();
        em.persist(item1);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.WAITING)
                .booker(user1)
                .item(item1)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getOwnerBookings(user1.getId(), "WAITING", null);
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.id = :id and bk.status = :status", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", booking.getId())
                .setParameter("status", BookingStatus.WAITING)
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }


    @Test
    void shouldGetOwnerBookingsWhenStateAllWithoutPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getOwnerBookings(user1.getId(), "ALL", null);
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.booker.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", user2.getId())
                .getResultList();

        assertEquals(2, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(1).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(1).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(1).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(1).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(1).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(1).getItem().getId());
        assertEquals(bookings.get(1).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(1).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(1).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(1).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(1).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(1).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetOwnerBookingsWhenStateRejectedWithoutPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getOwnerBookings(user1.getId(), "REJECTED", null);
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.booker.id = :id and bk.status = :status", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", user2.getId())
                .setParameter("status", BookingStatus.REJECTED)
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetOwnerBookingsWhenStateCurrentWithoutPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getOwnerBookings(user1.getId(), "CURRENT", null);
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", booking.getId())
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetOwnerBookingsWhenStatePastWithoutPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(10))
                .end(LocalDateTime.now().minusHours(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getOwnerBookings(user1.getId(), "PAST", null);
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", booking.getId())
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetOwnerBookingsWhenStateFutureWithoutPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(10))
                .end(LocalDateTime.now().minusHours(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getOwnerBookings(user1.getId(), "FUTURE", null);
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", booking2.getId())
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetOwnerBookingsWhenStateWaitingWithPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getOwnerBookings(user1.getId(), "WAITING", PageRequest.of(0, 1));
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.booker.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", user2.getId())
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(1).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(1).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(1).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(1).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(1).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(1).getItem().getId());
    }

    @Test
    void shouldGetOwnerBookingsWhenStateAllWithPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getOwnerBookings(user1.getId(), "ALL", PageRequest.of(0, 10));
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.booker.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", user2.getId())
                .getResultList();

        assertEquals(2, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(1).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(1).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(1).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(1).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(1).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(1).getItem().getId());
        assertEquals(bookings.get(1).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(1).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(1).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(1).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(1).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(1).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetOwnerBookingsWhenStateRejectedWithPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getOwnerBookings(user1.getId(), "REJECTED", PageRequest.of(0, 10));
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.booker.id = :id and bk.status = :status", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", user2.getId())
                .setParameter("status", BookingStatus.REJECTED)
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetOwnerBookingsWhenStateCurrentWithPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getOwnerBookings(user1.getId(), "CURRENT", PageRequest.of(0, 10));
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", booking.getId())
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetOwnerBookingsWhenStatePastWithPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(10))
                .end(LocalDateTime.now().minusHours(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getOwnerBookings(user1.getId(), "PAST", PageRequest.of(0, 10));
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", booking.getId())
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }

    @Test
    void shouldGetOwnerBookingsWhenStateFutureWithPagingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(10))
                .end(LocalDateTime.now().minusHours(2))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        em.persist(booking2);

        List<ResponseBookingDto> bookings = service
                .getOwnerBookings(user1.getId(), "FUTURE", PageRequest.of(0, 10));
        TypedQuery<Booking> query = em.createQuery("SELECT bk from Booking bk " +
                "where bk.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", booking2.getId())
                .getResultList();

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(0).getItem().getId());
    }
}