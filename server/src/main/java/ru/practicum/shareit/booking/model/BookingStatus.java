package ru.practicum.shareit.booking.model;

import java.util.Optional;

/**
 * Статус бронирования вещи. Может принимать одно из следующих значений:
 * <p><b>WAITING</b> — новое бронирование, ожидает одобрения;</p>
 * <p><b>APPROVED</b> — бронирование подтверждено владельцем;</p>
 * <p><b>REJECTED</b> — бронирование отклонено владельцем;</p>
 * <p><b>CANCELED</b> — бронирование отменено создателем.</p>
 *
 * @author Igor Ivanov
 */
public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    public static Optional<BookingStatus> from(String stringStatus) {
        for (BookingStatus status : values()) {
            if (status.name().equalsIgnoreCase(stringStatus)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}