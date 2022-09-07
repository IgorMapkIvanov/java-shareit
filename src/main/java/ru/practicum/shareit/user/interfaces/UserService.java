package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.interfaces.Validations;

import java.util.List;

public interface UserService<T> extends Validations<T> {
    List<T> getAll();

    T getById(Long id);

    T add(T t);

    T update(T t);

    void delete(Long id);
}