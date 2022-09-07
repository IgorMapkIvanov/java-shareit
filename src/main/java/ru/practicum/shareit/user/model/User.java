package ru.practicum.shareit.user.model;

import lombok.*;

/**
 * Класс User со свойствами:<br>
 * <b>ID</b> — уникальный идентификатор пользователя;<br>
 * <b>Name</b> — Поле имя или логин пользователя;<br>
 * <b>Email</b> — Поле электроной почты пользователя.<br>
 * <br>
 * Уникальность определяется по ID пользователя.<br>
 * Класс поддерживает {@link Builder}.<br>
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
public class User {
    private Long id;
    private String name;
    private String email;
}