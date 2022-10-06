package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

/**
 * Класс <b>Item</b> со свойствами:
 * <p><b>ID</b> — Поле уникальный идентификатор вещи;</p>
 * <p><b>Name</b> — Поле краткое название. Обязвтельное поле, размер не больше 100 символов;</p>
 * <p><b>Description</b> — Поле развёрнутое описание. Размер не больше 500 символов;</p>
 * <p><b>available</b> — Поле статус о том, доступна или нет вещь для аренды: true - доступна, false - нет;</p>
 * <p><b>Owner</b> — Владелец вещи, объект класс {@link User};<br>
 * <p><b>Request</b> — Поле ID запроса, по которому была создана вещь. Значение 0 - вещь была создана не по запросу.</p>
 * <p>Уникальность определяется по ID вещи.</p>
 * <p>Класс поддерживает {@link Builder}. Значения по умолчанию: <b>Available = true</b>, <b>Request = 0</b>.</p>
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
public class Item {
    private Long id;
    private String name;
    private String description;
    @Builder.Default
    private Boolean available = true;
    private User owner;
    @Builder.Default
    private Long request = 0L;
}