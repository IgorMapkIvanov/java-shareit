package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.interfaces.Create;
import ru.practicum.shareit.interfaces.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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

    @NotBlank(groups = Create.class, message = "Имя не должно быть пустым или содержать только пробелы.")
    @Size(max = 50,
            groups = {Create.class, Update.class},
            message = "Имя или логин не должно быть больше 50 символов.")
    private String name;

    @NotBlank(groups = Create.class,
            message = "E-mail не должен быть пустым или содержать только пробелы.")
    @Size(max = 150,
            groups = {Create.class, Update.class},
            message = "Имя или логин не должно быть больше 50 символов.")
    @Email(groups = {Create.class, Update.class},
            message = "Не корректный e-mail.")
    private String email;
}