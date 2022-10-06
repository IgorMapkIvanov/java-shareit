package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.Create;
import ru.practicum.shareit.interfaces.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс контроллер для пути "/users".
 * <p>Взаимодействует с сервисным слоем {@link UserService}
 *
 * @author Igor Ivanov
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    // GET запросы.

    /**
     * Метод обработки запроса на получение списка всех пользователей.
     *
     * @return {@link List} содержащий {@link UserDto}
     */
    @GetMapping
    public List<UserDto> getAll() {
        log.info("CONTROLLER: Запрос на получение списка пользователей.");
        return userService.getAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Метод обработки запроса на получение информации о пользователе по его ID.
     *
     * @param id ID пользователя.
     * @return {@link UserDto}
     */
    @GetMapping("/{id}")
    public UserDto getById(@PathVariable @Positive(message = "ID пользователя должен быть положительным.") Long id) {
        log.info("CONTROLLER: Запрос на получение информации о пользователе с ID = {}.", id);
        return UserMapper.toDto(userService.getById(id));
    }

    // POST запросы

    /**
     * Метод обработки запроса на добавление нового пользователя.
     *
     * @param userDto {@link UserDto} без ID.
     * @return {@link UserDto} c ID.
     */
    @PostMapping
    public UserDto add(@Validated(value = Create.class) @RequestBody UserDto userDto) {
        log.info("CONTROLLER: Запрос на добавление нового пользователя: {}.", userDto);
        User user = UserMapper.fromDto(userDto);
        userService.add(user);
        return UserMapper.toDto(user);
    }

    // PATCH запросы

    /**
     * Метод обработки запроса на обновление данных пользователя.
     *
     * @param id      ID пользователя
     * @param userDto {@link UserDto} с новыми значениями.
     * @return {@link UserDto} обновленная информация о пользователе.
     */
    @PatchMapping("/{id}")
    public UserDto update(@PathVariable @Positive Long id, @Validated(value = Update.class) @RequestBody UserDto userDto) {
        log.info("CONTROLLER: Запрос на обновление пользователя с ID = {}.", id);
        userDto.setId(id);
        User user = UserMapper.fromDto(userDto);
        return UserMapper.toDto(userService.update(user));
    }

    // DELETE запросы

    /**
     * Метод обработки запроса на удаление пользователя.
     *
     * @param id ID пользователя.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive Long id) {
        log.info("CONTROLLER: Запрос на удаление пользователя с ID = {}.", id);
        userService.delete(id);
    }
}