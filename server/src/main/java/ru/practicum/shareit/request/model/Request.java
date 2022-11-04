package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Класс <b>Request</b> со свойствами:
 * <p><b>ID</b> — Поле уникальный идентификатор запроса;</p>
 * <p><b>Description</b> — Поле текст запроса, содержащий описание требуемой вещи.
 * Обязательное поле, размер не больше 200 символов;</p>
 * <p><b>Requestor</b> — Пользователь, создающий запрос. Объект класса {@link User};</p>
 * <p><b>Created</b> — Поле дата и время создания запроса.</p>
 * <p>Уникальность определяется по ID запроса.</p>
 * <p>Класс поддерживает {@link Builder}.</p>
 *
 * @author Igor Ivanov
 */

@Entity
@Table(name = "requests", schema = "public")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description")
    private String description;
    @ManyToOne(cascade = CascadeType.ALL)
    private User requester;
    @Column(name = "created")
    private LocalDateTime created;
}