package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

/**
 * Класс определяет с методами преобразования объекта класса <b>{@link User}</b> в DTO класса <b>{@link UserDto}</b>
 * и наоборот.
 *
 * @author Igor Ivanov
 */
@Component
public class UserMapper {
    /**
     * Метод преобразует объект класса {@link User} в его DTO {@link UserDto}.
     *
     * @param user объект класса
     * @return {@link UserDto} - DTO объекта класса
     */
    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    /**
     * Метод преобразования DTO объекта класса {@link UserDto} в объект класса {@link User}.
     *
     * @param userDto DTO объекта класса
     * @return {@link User} - объект класса
     */
    public static User fromDto(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}