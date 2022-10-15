package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.interfaces.Create;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    // GET запросы
    @GetMapping
    public List<ResponseBookingDto> getUserBookings(
            @Positive @NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Positive Integer size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        log.info("CONTROLLER: Запрос на получение списка бронирований пользователя с ID = {}.", userId);
        return bookingService.getUserBookings(userId, state, pageRequest);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getBooking(@Positive @NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Positive @PathVariable Long bookingId) {
        log.info("CONTROLLER: Запрос на получение информации о бронировании с ID = {} пользователя с ID = {}.",
                bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getOwnerBookings(
            @Positive @NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Positive Integer size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        log.info("CONTROLLER: Запрос на получение информации о бронированиях пользователя с ID = {}.", userId);
        return bookingService.getOwnerBookings(userId, state, pageRequest);
    }

    //POST запросы
    @PostMapping
    public BookingDto addBooking(@Positive @NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Validated(value = Create.class) @RequestBody BookingDto bookingDto) {
        log.info("CONTROLLER: Запрос на бронирование вещи от пользователя с ID = {}.", userId);
        return bookingService.addBooking(userId, bookingDto);
    }

    //PATCH запросы
    @PatchMapping("/{bookingId}")
    public ResponseBookingDto approveBooking(@Positive @NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Positive @PathVariable Long bookingId,
                                             @NotNull @RequestParam Boolean approved) {
        log.info("CONTROLLER: Запрос на подтверждение бронирования с ID = {} пользователем с ID = {}.",
                bookingId,
                userId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    //DELETE запросы
    @DeleteMapping("/{bookingId}")
    public void delete(@Positive @NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                       @Positive @PathVariable Long bookingId) {
        log.info("CONTROLLER: Запрос на удаление бронирования с ID = {} пользователя с ID = {}.", bookingId, userId);
        bookingService.delete(userId, bookingId);
    }
}