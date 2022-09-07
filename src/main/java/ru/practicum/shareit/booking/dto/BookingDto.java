package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull
    @Positive
    private Item item;
    @NotNull
    @Positive
    private User booker;
    @NotNull
    @Builder.Default
    private BookingStatus status = BookingStatus.WAITING;
}