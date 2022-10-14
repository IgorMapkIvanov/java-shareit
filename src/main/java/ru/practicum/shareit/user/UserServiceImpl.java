package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Класс сервис, осуществляет бизнес логику работы с классом {@link User}.
 * <p>Взаимодействует с хранилищем {@link ru.practicum.shareit.user.UserRepository}
 *
 * @author Igor Ivanov
 */

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAll() {
        log.info("SERVICE: Запрос на получение списка пользователей.");
        return userRepository.findAll();
    }

    @Override
    public User getById(Long id) {
        log.info("SERVICE: Запрос на получение информации о пользователе с ID = {}.", id);
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow();
    }

    @Transactional
    @Override
    public User add(User user) {
        log.info("SERVICE: Запрос на добавление нового пользователя: {}.", user);
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User update(User user) {
        log.info("SERVICE: Запрос на обновление пользователя с ID = {}.", user.getId());
        User userBd = userRepository.findById(user.getId()).orElseThrow();
        copyFields(user, userBd);
        return userRepository.save(userBd);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        log.info("SERVICE: Запрос на удаление пользователя с ID = {}.", id);
        userRepository.findById(id).orElseThrow();
        userRepository.deleteById(id);
    }

    private void copyFields(User user, User userDb) {
        if (user.getName() != null && !user.getName().equals(userDb.getName())) {
            userDb.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().equals(userDb.getEmail())) {
            userDb.setEmail(user.getEmail());
        }
    }
}