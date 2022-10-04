package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для класса {@link User}.
 *
 * @author Igor Ivanov
 */

public interface UserRepository<T> {
    /**
     * Метод получения списка c информацией о всех T.
     *
     * @return список ({@link List}) всех T.
     */
    List<T> getAll();

    /**
     * Метод получения информации о T по ID.
     *
     * @return {@link Optional} c T.
     */
    Optional<T> getById(Long id);

    /**
     * Метод добавления T.
     *
     * @return T.
     */
    T add(T t);

    /**
     * Метод обновления T.
     *
     * @return T обновленный.
     */
    T update(T t);

    /**
     * Метод удаления T.
     */
    void delete(Long id);
}