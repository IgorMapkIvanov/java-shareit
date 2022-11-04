package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.model.User;

/**
 * Класс <b>ItemDto</b> со свойствами:
 * <p><b>ID</b> — Поле уникальный идентификатор вещи;</p>
 * <p><b>Name</b> — Поле краткое название. Обязательное поле, размер не больше 150 символов;</p>
 * <p><b>Description</b> — Поле развёрнутое описание. Размер не больше 500 символов;</p>
 * <p><b>available</b> — Поле статус о том, доступна или нет вещь для аренды: true - доступна, false - нет;</p>
 * <p><b>Owner</b> — Владелец вещи, объект класс {@link User};<br>
 * <p><b>Request</b> — Поле ID запроса, по которому была создана вещь. Значение 0 - вещь была создана не по запросу.</p>
 * <p>Класс поддерживает {@link Builder}. Значения по умолчанию: <b>Available = true</b>, <b>Request = 0</b>.</p>
 *
 * @author Igor Ivanov
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Long requestId;
}