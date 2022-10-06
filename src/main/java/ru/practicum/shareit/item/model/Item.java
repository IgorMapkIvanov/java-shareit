package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

/**
 * Класс <b>Item</b> со свойствами:
 * <p><b>ID</b> — Поле уникальный идентификатор вещи;</p>
 * <p><b>Name</b> — Поле краткое название. Обязательное поле, размер не больше 100 символов;</p>
 * <p><b>Description</b> — Поле развёрнутое описание. Размер не больше 500 символов;</p>
 * <p><b>available</b> — Поле статус о том, доступна или нет вещь для аренды: true - доступна, false - нет;</p>
 * <p><b>Owner</b> — Владелец вещи, объект класс {@link User};<br>
 * <p><b>Request</b> — Поле ID запроса, по которому была создана вещь. Значение 0 - вещь была создана не по запросу.</p>
 * <p>Уникальность определяется по ID вещи.</p>
 * <p>Класс поддерживает {@link Builder}. Значения по умолчанию: <b>Available = true</b>, <b>Request = 0</b>.</p>
 *
 * @author Igor Ivanov
 */
@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint")
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false, length = 500)
    private String description;
    @Column(name = "available", columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean available = true;
    @ManyToOne(targetEntity = User.class, cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE})
    private User owner;
    @Column(name = "request")
    @Builder.Default
    private Long request = 0L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return id != null && id.equals(((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}