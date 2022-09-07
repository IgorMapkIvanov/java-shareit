package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Класс <b>ItemRequest</b> со свойствами:
 * <p><b>ID</b> — Поле уникальный идентификатор запроса;</p>
 * <p><b>Description</b> — Поле текст запроса, содержащий описание требуемой вещи.
 * Обязвтельное поле, размер не больше 200 символов;</p>
 * <p><b>Requestor</b> — Пользователь, создавщий запрос. Объект класса {@link User};</p>
 * <p><b>Created</b> — Поле дата и время создания запроса.</p>
 * <p>Уникальность определяется по ID запроса.</p>
 * <p>Класс поддерживает {@link Builder}.</p>
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
public class ItemRequest {
    private Long id;
    @NotBlank
    @Max(200)
    private String description;
    @NotNull
    private User requestor;
    @NotNull
    private LocalDateTime created;
}