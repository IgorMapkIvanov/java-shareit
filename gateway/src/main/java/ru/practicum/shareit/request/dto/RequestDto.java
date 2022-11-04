package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.interfaces.Create;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс <b>Request</b> со свойствами:
 * <p><b>ID</b> — Поле уникальный идентификатор запроса;</p>
 * <p><b>Description</b> — Поле текст запроса, содержащий описание требуемой вещи.
 * Обязательное поле, размер не больше 200 символов;</p>
 * <p><b>Requestor</b> — Поле ID пользователя, создавшего запрос;</p>
 * <p><b>Created</b> — Поле дата и время создания запроса.</p>
 * <p>Класс поддерживает {@link Builder}.</p>
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
public class RequestDto {
    private Long id;
    @NotBlank(groups = Create.class)
    @Max(255)
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}