package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
//@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<ResponseBookingDto> getUserBookings(Long userId, String bookingState, PageRequest pageRequest) {
        log.info("SERVICE: Обработка запроса на получение списка бронирований пользователя с ID = {}.", userId);
        validationUserIdAndBookingState(userId, bookingState);
        List<Booking> bookings = getUserBookingList(userId, bookingState, pageRequest);

        log.info("SERVICE: Отправка списка бронирований пользователя с ID = {}.", userId);
        return bookings
                .stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public ResponseBookingDto getBooking(Long userId, Long bookingId) {
        log.info("SERVICE: Обработка запроса на получение информации о бронировании с ID = {} пользователя с ID = {}.",
                bookingId, userId);
        Booking booking = bookingRepository.findBookingById(bookingId).orElseThrow(() -> {
            log.error("SERVICE: Бронирование с ID = {} - не найден.", bookingId);
            throw new NotFoundException("Бронирование с ID = " + bookingId + " не найдено.");
        });

        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            log.error("SERVICE: Бронирование с ID = {} не принадлежит пользователю с ID = {}.", bookingId, userId);
            throw new NotFoundException("Бронирование с ID = " + bookingId + " не принадлежит пользователю с ID = " +
                    userId + ".");
        }

        log.info("SERVICE: Отправка информации о бронировании с ID = {} пользователя с ID = {}.",
                bookingId, userId);
        return BookingMapper.toResponseBookingDto(booking);
    }

    @Override
    public List<ResponseBookingDto> getOwnerBookings(Long userId, String bookingState, PageRequest pageRequest) {
        log.info("CONTROLLER: Обработка запроса на получение информации о бронированиях пользователя с ID = {}.", userId);
        validationUserIdAndBookingState(userId, bookingState);
        List<Booking> bookings = getOwnerBookingsList(userId, bookingState, pageRequest);

        log.info("CONTROLLER: Отправка информации о бронированиях пользователя с ID = {}.", userId);
        return bookings
                .stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
//    @Transactional
    public BookingDto addBooking(Long userId, BookingDto bookingDto) {
        log.info("SERVICE: Обработка запроса на бронирование вещи от пользователя с ID = {}.", userId);
        dataValidation(userId, bookingDto);
        Booking booking = BookingMapper.fromDto(bookingDto);

        booking.setBooker(userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("SERVICE: Пользователь с ID = {} - не найден.", userId);
                    throw new NotFoundException("Пользователь с ID = " + userId + "- не найден.");
                }));

        booking.setItem(itemRepository.getItemById(bookingDto.getItemId())
                .orElseThrow(() -> {
                    log.error("SERVICE: Вещь с ID = {} - не найдена.", bookingDto.getItemId());
                    throw new NotFoundException("Вещь с ID = " + bookingDto.getItemId() + " не найдена.");
                }));
        booking.setStatus(BookingStatus.WAITING);

        log.info("SERVICE: Отправка информации о запросе на бронирование вещи от пользователя с ID = {}.", userId);
        bookingRepository.save(booking);
        return BookingMapper.toDto(booking);
    }

    @Override
//    @Transactional
    public ResponseBookingDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        log.info("SERVICE: Обработка запроса на подтверждение бронирования с ID = {} пользователем с ID = {}.",
                bookingId,
                userId);
        Booking booking = bookingRepository.findBookingById(bookingId).orElseThrow(() -> {
            log.error("SERVICE: Бронирование с ID = {} - не найден.", bookingId);
            throw new NotFoundException("Бронирование с ID = " + bookingId + " не найдено.");
        });

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.error("SERVICE: Нельзя сменить статус бронирования чужой вещи.");
            throw new NotFoundException("Нельзя сменить статус бронирования чужой вещи.");
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.error("SERVICE: Бронирование уже в статусе {}", BookingStatus.APPROVED);
            throw new BadRequestException("Бронирование уже в статусе " + BookingStatus.APPROVED + ".");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        log.info("SERVICE: Отправка подтверждения бронирования с ID = {} пользователем с ID = {}.",
                bookingId,
                userId);
        return BookingMapper.toResponseBookingDto(bookingRepository.save(booking));
    }

    private void validationUserIdAndBookingState(Long userId, String bookingState) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("SERVICE: Пользователь с ID = {} - не найден.", userId);
                    throw new NotFoundException("Пользователь с ID = " + userId + "- не найден.");
                });
        try {
            BookingState.valueOf(bookingState);
        } catch (IllegalArgumentException e) {
            log.error("Unknown state: " + bookingState);
            throw new ValidationException("Unknown state: " + bookingState);
        }
    }

    private List<Booking> getUserBookingList(Long userId, String bookingState, PageRequest pageRequest) {
        switch (BookingState.valueOf(bookingState)) {
            case ALL:
                return bookingRepository.findBookingsByBooker_IdOrderByStartDesc(userId, pageRequest);
            case WAITING:
                return bookingRepository
                        .findBookingsByBooker_IdAndStatusEqualsOrderByStartDesc(
                                userId,
                                BookingStatus.WAITING,
                                pageRequest);
            case PAST:
                return bookingRepository
                        .findBookingsByBooker_IdAndStartBeforeAndEndBeforeOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now(), pageRequest);
            case FUTURE:
                return bookingRepository
                        .findBookingsByBooker_IdAndStartAfterAndEndAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now(), pageRequest);
            case REJECTED:
                return bookingRepository
                        .findBookingsByBooker_IdAndStatusEqualsOrderByStartDesc(
                                userId,
                                BookingStatus.REJECTED,
                                pageRequest);
            case CURRENT:
                return bookingRepository
                        .findBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now(), pageRequest);
            default:
                return Collections.emptyList();
        }
    }

    private List<Booking> getOwnerBookingsList(Long userId, String bookingState, PageRequest pageRequest) {
        switch (BookingState.valueOf(bookingState)) {
            case CURRENT:
                return bookingRepository
                        .findBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now(), pageRequest);
            case FUTURE:
                return bookingRepository
                        .findBookingsByItemOwnerIdAndStartAfterAndEndAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now(), pageRequest);
            case PAST:
                return bookingRepository
                        .findBookingsByItemOwnerIdAndStartBeforeAndEndBeforeOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now(), pageRequest);
            case WAITING:
                return bookingRepository
                        .findBookingsByItemOwnerIdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.WAITING, pageRequest);
            case REJECTED:
                return bookingRepository
                        .findBookingsByItemOwnerIdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.REJECTED, pageRequest);
            case ALL:
                return bookingRepository.findBookingsByItemOwnerIdOrderByStartDesc(userId, pageRequest);
            default:
                return Collections.emptyList();
        }
    }

    private void dataValidation(Long userId, BookingDto bookingDto) {
        Item item = itemRepository.getItemById(bookingDto.getItemId()).orElseThrow(() -> {
            log.error("SERVICE: Вещь с ID = {} - не найдена.", bookingDto.getItemId());
            throw new NotFoundException("Вещь с ID = " + bookingDto.getItemId() + " не найдена.");
        });

        userRepository.findById(userId).orElseThrow(() -> {
            log.error("SERVICE: Пользователь с ID = {} - не найден.", userId);
            throw new NotFoundException("Пользователь с ID = " + userId + "- не найден.");
        });

        if (item.getOwner().getId().equals(userId)) {
            log.error("SERVICE: Пользователь с ID = {} не может бронировать свою вещь.", userId);
            throw new NotFoundException("Пользователь с ID = " + userId + " не может бронировать свою вещь.");
        }

        if (!item.getAvailable()) {
            log.error("SERVICE: Вещь не доступна для бронирования.");
            throw new BadRequestException("Вещь не доступна для бронирования.");
        }

        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            log.error("SERVICE: Дата начала бронирования раньше даты окончания бронирования.");
            throw new BadRequestException("Дата начала бронирования раньше даты окончания бронирования.");
        }
    }
}