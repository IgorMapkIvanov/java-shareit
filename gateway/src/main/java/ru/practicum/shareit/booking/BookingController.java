package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.interfaces.Create;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingClient client;

    // GET запросы
    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") @Positive @NotNull Long userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Positive Integer size) {
        BookingState.from(state).orElseThrow(() -> new ValidationException("Unknown state: " + state));
        log.info("GATEWAY: Запрос на получение списка бронирований пользователя с ID = {}.", userId);
        return client.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") @Positive @NotNull Long userId,
                                             @Positive @PathVariable Long bookingId) {
        log.info("GATEWAY: Запрос на получение информации о бронировании с ID = {} пользователя с ID = {}.",
                bookingId, userId);
        return client.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") @Positive @NotNull Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Positive Integer size) {
        BookingState.from(state).orElseThrow(() -> new ValidationException("Unknown state: " + state));
        log.info("GATEWAY: Запрос на получение информации о бронированиях пользователя с ID = {}.", userId);
        return client.getOwnerBookings(userId, state, from, size);
    }

    //POST запросы
    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") @Positive @NotNull Long userId,
                                             @Validated(value = Create.class) @RequestBody BookingDto bookingDto) {
        log.info("GATEWAY: Запрос на бронирование вещи от пользователя с ID = {}.", userId);
        return client.addBooking(userId, bookingDto);
    }

    //PATCH запросы
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") @Positive @NotNull Long userId,
                                                 @Positive @PathVariable Long bookingId,
                                                 @NotNull @RequestParam Boolean approved) {
        log.info("GATEWAY: Запрос на подтверждение бронирования с ID = {} пользователем с ID = {}.",
                bookingId,
                userId);
        return client.approveBooking(userId, bookingId, approved);
    }
}