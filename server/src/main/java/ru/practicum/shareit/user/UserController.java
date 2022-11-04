package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

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
@Validated
public class UserController {
    private final UserService userService;

    // GET запросы.

    /**
     * Метод обработки запроса на получение списка всех пользователей.
     *
     * @return {@link List} содержащий {@link UserDto}
     */
    @GetMapping
    public List<UserDto> getAll(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("CONTROLLER: Запрос на получение списка пользователей. from = {}, size = {}", from, size);
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return userService.getAll(pageRequest);
    }

    /**
     * Метод обработки запроса на получение информации о пользователе по его ID.
     *
     * @param id ID пользователя.
     * @return {@link UserDto}
     */
    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("CONTROLLER: Запрос на получение информации о пользователе с ID = {}.", id);
        return userService.getById(id);
    }

    // POST запросы

    /**
     * Метод обработки запроса на добавление нового пользователя.
     *
     * @param userDto {@link UserDto} без ID.
     * @return {@link UserDto} c ID.
     */
    @PostMapping
    public UserDto add(@RequestBody UserDto userDto) {
        log.info("CONTROLLER: Запрос на добавление нового пользователя: {}.", userDto);
        return userService.add(userDto);
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
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("CONTROLLER: Запрос на обновление пользователя с ID = {}.", id);
        userDto.setId(id);
        return userService.update(userDto);
    }

    // DELETE запросы

    /**
     * Метод обработки запроса на удаление пользователя.
     *
     * @param id ID пользователя.
     */
    @DeleteMapping("/{id}")
    public UserDto delete(@PathVariable Long id) {
        log.info("CONTROLLER: Запрос на удаление пользователя с ID = {}.", id);
        return userService.delete(id);
    }
}