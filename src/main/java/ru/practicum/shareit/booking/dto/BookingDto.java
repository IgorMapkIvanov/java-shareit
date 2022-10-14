package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.interfaces.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * Класс <b>BookingDto</b> со свойствами:
 * <p><b>ID</b> — Поле уникальный идентификатор бронирования;</p>
 * <p><b>Start</b> — Поле дата и время начала бронирования;</p>
 * <p><b>End</b> — Поле дата и время конца бронирования;</p>
 * <p><b>Item</b> — Поле ID вещи, которую пользователь бронирует;</p>
 * <p><b>Booker</b> — Поле ID пользователя, который осуществляет бронирование;</p>
 * <p><b>Status</b> — Поле статус бронирования ({@link BookingStatus}).</p>
 * <p>Класс поддерживает {@link Builder}. Значения по умолчанию: <b>Status = {@link BookingStatus#WAITING WAITING}</b>.</p>
 *
 * @author Igor Ivanov
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    @NotNull(groups = Create.class)
    @FutureOrPresent(groups = Create.class)
    private LocalDateTime start;
    @NotNull(groups = Create.class)
    @Future(groups = Create.class)
    private LocalDateTime end;
    @NotNull(groups = Create.class)
    @Positive(groups = Create.class)
    private Long itemId;
    private BookingStatus status;
}