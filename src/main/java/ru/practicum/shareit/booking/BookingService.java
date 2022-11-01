package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.util.List;

public interface BookingService {
    List<ResponseBookingDto> getUserBookings(Long userId, String bookingState, PageRequest pageRequest);

    ResponseBookingDto getBooking(Long userId, Long bookingId);

    List<ResponseBookingDto> getOwnerBookings(Long userId, String state, PageRequest pageRequest);

    BookingDto addBooking(Long userId, BookingDto bookingDto);

    ResponseBookingDto approveBooking(Long userId, Long bookingId, Boolean approved);
}