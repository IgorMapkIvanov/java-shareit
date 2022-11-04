package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.Create;
import ru.practicum.shareit.interfaces.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Класс контроллер для пути "/users".
 *
 * @author Igor Ivanov
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserClient client;

    // GET запросы.

    /**
     * Метод обработки запроса на получение списка всех пользователей.
     *
     * @return {@link ResponseEntity}
     */
    @GetMapping
    public ResponseEntity<Object> getAll(@RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Long from,
                                         @RequestParam(name = "size", defaultValue = "10") @Positive Long size) {
        log.info("GATEWAY: Запрос на получение списка пользователей. from = {}, size = {}", from, size);
        return client.getAll(from, size);
    }

    /**
     * Метод обработки запроса на получение информации о пользователе по его ID.
     *
     * @param id ID пользователя.
     * @return {@link ResponseEntity}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable @Positive(message = "ID пользователя должен быть положительным.") Long id) {
        log.info("GATEWAY: Запрос на получение информации о пользователе с ID = {}.", id);
        return client.getById(id);
    }

    // POST запросы

    /**
     * Метод обработки запроса на добавление нового пользователя.
     *
     * @param userDto {@link UserDto} без ID.
     * @return {@link ResponseEntity}.
     */
    @PostMapping
    public ResponseEntity<Object> add(@Validated(value = Create.class) @RequestBody UserDto userDto) {
        log.info("GATEWAY: Запрос на добавление нового пользователя: {}.", userDto);
        return client.add(userDto);
    }

    // PATCH запросы

    /**
     * Метод обработки запроса на обновление данных пользователя.
     *
     * @param id      ID пользователя
     * @param userDto {@link UserDto} с новыми значениями.
     * @return {@link ResponseEntity}.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable @Positive Long id, @Validated(value = Update.class) @RequestBody UserDto userDto) {
        log.info("GATEWAY: Запрос на обновление пользователя с ID = {}.", id);
        return client.update(id, userDto);
    }

    // DELETE запросы

    /**
     * Метод обработки запроса на удаление пользователя.
     *
     * @param id ID пользователя.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive Long id) {
        log.info("GATEWAY: Запрос на удаление пользователя с ID = {}.", id);
        client.deleteById(id);
    }
}