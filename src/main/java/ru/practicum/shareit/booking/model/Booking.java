package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
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
@Entity
@Table(name = "bookings", schema = "public")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint")
    private Long id;
    @Column(name = "booking_from")
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @Column(name = "booking_to")
    @NotNull
    @Future
    private LocalDateTime end;
    @ManyToOne(targetEntity = Item.class, cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE})
    private Item item;
    @ManyToOne(targetEntity = User.class, cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE})
    private User booker;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus status = BookingStatus.WAITING;
}