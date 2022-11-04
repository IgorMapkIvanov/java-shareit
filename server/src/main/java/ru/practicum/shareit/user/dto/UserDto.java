package ru.practicum.shareit.user.dto;

import lombok.*;

/**
 * Класс UserDto со свойствами:
 * <p><b>ID</b> — уникальный идентификатор пользователя;
 * <p><b>Name</b> — Поле имя или логин пользователя;
 * <p><b>Email</b> — Поле электронной почты пользователя.
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
public class UserDto {
    private Long id;
    private String name;
    private String email;
}