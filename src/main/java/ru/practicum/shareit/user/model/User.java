package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;

/**
 * Класс User со свойствами:<br>
 * <b>ID</b> — уникальный идентификатор пользователя;<br>
 * <b>Name</b> — Поле имя или логин пользователя;<br>
 * <b>Email</b> — Поле электронной почты пользователя.<br>
 * <br>
 * Уникальность определяется по ID пользователя.<br>
 * Класс поддерживает {@link Builder}.<br>
 *
 * @author Igor Ivanov
 */
@Entity
@Table(name = "USERS", schema = "public")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Column(name = "email", nullable = false, length = 150, unique = true)
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return id != null && id.equals(((User) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}