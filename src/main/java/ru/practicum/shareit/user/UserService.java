package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User getById(Long id);

    User add(User user);

    User update(User user);

    void delete(Long id);
}