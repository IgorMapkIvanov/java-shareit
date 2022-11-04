package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.interfaces.Create;
import ru.practicum.shareit.interfaces.Update;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    @NotBlank(groups = Create.class)
    @Size(max = 150,
            groups = {Create.class, Update.class},
            message = "Название вещи не должно быть больше 150 символов.")
    private String name;
    @NotBlank(groups = Create.class)
    @Size(max = 500,
            groups = {Create.class, Update.class},
            message = "Описание вещи не должно быть больше 500 символов.")
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;
    private User owner;
    private Long requestId;
}