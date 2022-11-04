package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<UserDto> getAll(PageRequest pageRequest) {
        log.info("SERVICE: Запрос на получение списка пользователей.");
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public UserDto getById(Long id) {
        log.info("SERVICE: Запрос на получение информации о пользователе с ID = {}.", id);
        Optional<User> user = userRepository.findById(id);
        return UserMapper.toDto(user.orElseThrow());
    }

    @Transactional
    @Override
    public UserDto add(UserDto userDto) {
        log.info("SERVICE: Запрос на добавление нового пользователя: {}.", userDto);
        return UserMapper.toDto(userRepository.save(UserMapper.fromDto(userDto)));
    }

    @Transactional
    @Override
    public UserDto update(UserDto userDto) {
        log.info("SERVICE: Запрос на обновление пользователя с ID = {}.", userDto.getId());
        User userBd = userRepository.findById(userDto.getId()).orElseThrow();
        copyFields(UserMapper.fromDto(userDto), userBd);
        return UserMapper.toDto(userRepository.save(userBd));
    }

    @Transactional
    @Override
    public UserDto delete(Long id) {
        log.info("SERVICE: Запрос на удаление пользователя с ID = {}.", id);
        User deleteUser = userRepository.findById(id).orElseThrow();
        userRepository.deleteById(id);
        return UserMapper.toDto(deleteUser);
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