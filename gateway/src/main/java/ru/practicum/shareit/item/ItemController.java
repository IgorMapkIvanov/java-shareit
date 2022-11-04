package ru.practicum.shareit.item;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.Create;
import ru.practicum.shareit.interfaces.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Класс контроллер для пути "/items".
 *
 * @author Igor Ivanov
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Validated
public class ItemController {
    private final ItemClient client;

    // GET запросы.

    /**
     * Метод обработки запроса на получение списка всех вещей пользователя.
     *
     * @param userId ID пользователя, передается через заголовок запроса "X-Sharer-User-Id".
     * @return {@link ResponseEntity}
     */
    @GetMapping
    public ResponseEntity<Object> getAllItemsForOwnerWithId(
            @RequestHeader("X-Sharer-User-Id") @NonNull @Positive Long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Positive Integer size) {
        log.info("GATEWAY: Запрос на получение списка с информацией всех вещей пользователя с ID = {}.", userId);
        return client.getAllItemsForOwnerWithId(userId, from, size);
    }

    /**
     * Метод обработки запроса на получение информации о вещи пользователя.
     *
     * @param itemId ID вещи, передается через переменную пути.
     * @return {@link ResponseEntity}
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") @NonNull @Positive Long userId,
                                              @PathVariable @Positive(message = "ID вещи должен быть положительным.") Long itemId) {
        log.info("GATEWAY: Запрос на получение информации о вещи с ID = {}.", itemId);
        return client.getItemById(userId, itemId);
    }

    /**
     * Метод обработки запроса на поиск вещи.
     *
     * @param text текс запроса поиска.
     * @return {@link ResponseEntity}
     */
    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByText(
            @RequestHeader("X-Sharer-User-Id") @NonNull @Positive Long userId,
            @RequestParam(value = "text", defaultValue = "") String text,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Positive Integer size) {
        log.info("GATEWAY: Запрос на поиск вещи в имени или описании содержащей текст: {}.", text);
        return client.searchItemByText(text, userId, from, size);
    }

    // POST запросы

    /**
     * Метод обработки запроса на добавление новой вещи пользователя.
     *
     * @param itemDto объект класса {@link ItemDto}, передается через тело запроса.
     * @param userId  ID владельца вещи, передается через заголовок запроса "X-Sharer-User-Id".
     * @return {@link ResponseEntity}.
     */
    @PostMapping
    public ResponseEntity<Object> addItemForUserWithId(@RequestHeader("X-Sharer-User-Id") @NonNull @Positive Long userId,
                                                       @Validated(value = Create.class) @RequestBody ItemDto itemDto) {
        log.info("GATEWAY: Запрос на добавление новой вещи: {} для пользователя с ID = {}.", itemDto, userId);
        return client.addItem(userId, itemDto);
    }

    /**
     * Метод обработки запроса на добавление комментария для вещи.
     *
     * @param commentDto объект класса {@link CommentDto}, передается через тело запроса.
     * @param userId     ID владельца вещи, передается через заголовок запроса "X-Sharer-User-Id".
     * @param itemId     ID вещи, передается через переменную пути.
     * @return {@link ResponseEntity}.
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Validated(value = Create.class) @RequestBody CommentDto commentDto,
                                             @Positive @PathVariable Long itemId) {
        log.info("GATEWAY: Запрос на добавление комментария: {} для вещи с ID = {}.", commentDto, itemId);
        return client.addComment(userId, commentDto, itemId);
    }
    // PATCH запросы

    /**
     * Метод обработки запроса на обновление данных вещи.
     *
     * @param userId  ID владельца вещи, передается через заголовок запроса "X-Sharer-User-Id".
     * @param itemId  ID вещи, передается через переменную пути.
     * @param itemDto {@link ItemDto} с новыми значениями.
     * @return {@link ResponseEntity} .
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItemForUserWithId(
            @RequestHeader("X-Sharer-User-Id") @NonNull @Positive Long userId,
            @PathVariable @Positive Long itemId, @Validated(value = Update.class) @RequestBody ItemDto itemDto) {
        log.info("GATEWAY: Запрос на обновление вещи с ID = {} пользователя с ID = {}. {}", itemId, userId, itemDto);
        return client.updateItemForUserWithId(userId, itemId, itemDto);
    }
}