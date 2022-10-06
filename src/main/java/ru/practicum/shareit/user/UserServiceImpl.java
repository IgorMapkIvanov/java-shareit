package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailIsPresentException;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Класс сервис, осуществляет бизнес логику работы с классом {@link User}.
 * <p>Взаимодействует с хранилищем {@link UserRepository}
 *
 * @author Igor Ivanov
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService<User> {
    private final UserRepository<User> userRepository;

    @Override
    public List<User> getAll() {
        log.info("SERVICE: Запрос на получение списка пользователей.");
        return userRepository.getAll();
    }

    @Override
    public User getById(Long id) {
        log.info("SERVICE: Запрос на получение информации о пользователе с ID = {}.", id);
        Optional<User> user = userRepository.getById(id);
        return user.orElseThrow();
    }

    @Override
    public User add(User user) {
        log.info("SERVICE: Запрос на добавление нового пользователя: {}.", user);
        validation(user);
        return userRepository.add(user);
    }

    @Override
    public User update(User user) {
        log.info("SERVICE: Запрос на обновление пользователя с ID = {}.", user.getId());
        validation(user);
        userRepository.getById(user.getId()).orElseThrow();
        return userRepository.update(user);
    }

    @Override
    public void delete(Long id) {
        log.info("SERVICE: Запрос на удаление пользователя с ID = {}.", id);
        userRepository.getById(id).orElseThrow();
        userRepository.delete(id);
    }

    @Override
    public void validation(User user) {
        if (userRepository.getUniqueEmails().contains(user.getEmail())) {
            throw new EmailIsPresentException("Пользователь с таким e-mail: " + user.getEmail() + " существует.");
        }
    }
}