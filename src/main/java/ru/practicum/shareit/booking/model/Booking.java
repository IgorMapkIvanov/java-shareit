package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * Класс <b>Booking</b> со свойствами:
 * <p><b>ID</b> — Поле уникальный идентификатор бронирования;</p>
 * <p><b>Start</b> — Поле дата и время начала бронирования;</p>
 * <p><b>End</b> — Поле дата и время конца бронирования;</p>
 * <p><b>Item</b> — Вещь, объект класса {@link Item},, которую пользователь бронирует;</p>
 * <p><b>Booker</b> — Пользователь, объект класса {@link User}, который осуществляет бронирование;</p>
 * <p><b>Status</b> — Поле статус бронирования ({@link BookingStatus}).</p>
 * <p>Уникальность определяется по ID бронирования.</p>
 * <p>Класс поддерживает {@link Builder}. Значения по умолчанию: <b>Status = {@link BookingStatus#WAITING WAITING}</b>.</p>
 *
 * @author Igor Ivanov
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    @Builder.Default
    private BookingStatus status = BookingStatus.WAITING;
}