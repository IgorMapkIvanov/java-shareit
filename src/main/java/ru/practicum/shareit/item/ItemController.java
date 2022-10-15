package ru.practicum.shareit.item;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.Create;
import ru.practicum.shareit.interfaces.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * Класс контроллер для пути "/items".
 * <p>Взаимодействует с сервисным слоем {@link ItemService}
 *
 * @author Igor Ivanov
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    // GET запросы.

    /**
     * Метод обработки запроса на получение списка всех вещей пользователя.
     *
     * @param userId ID пользователя, передается через заголовок запроса "X-Sharer-User-Id".
     * @return {@link List} содержащий {@link ItemDto}
     */
    @GetMapping
    public List<ItemDtoWithBooking> getAllItemsForOwnerWithId(
            @RequestHeader("X-Sharer-User-Id") @NonNull @Positive Long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Positive Integer size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        log.info("CONTROLLER: Запрос на получение списка с информацией всех вещей пользователя с ID = {}.", userId);
        return itemService.getAllItemsForOwnerWithId(userId, pageRequest);
    }

    /**
     * Метод обработки запроса на получение информации о вещи пользователя.
     *
     * @param itemId ID вещи, передается через переменную пути.
     * @return {@link List} содержащий {@link ItemDto}
     */
    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItemById(@RequestHeader("X-Sharer-User-Id") @NonNull @Positive Long userId,
                                          @PathVariable @Positive(message = "ID вещи должен быть положительным.") Long itemId) {
        log.info("CONTROLLER: Запрос на получение информации о вещи с ID = {}.", itemId);
        return itemService.getItemById(userId, itemId);
    }

    /**
     * Метод обработки запроса на поиск вещи.
     *
     * @param text текс запроса поиска.
     * @return {@link ItemDto}
     */
    @GetMapping("/search")
    public List<ItemDto> searchItemsByNameOrDescriptionContainingTextIgnoreCaseAndAvailable(
            @RequestParam(value = "text", defaultValue = "") String text,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Positive Integer size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        log.info("CONTROLLER: Запрос на поиск вещи в имени или описании содержащей текст: {}.", text);
        return itemService.searchItemByText(text, pageRequest);
    }

    // POST запросы

    /**
     * Метод обработки запроса на добавление новой вещи пользователя.
     *
     * @param itemDto объект класса {@link ItemDto}, передается через тело запроса.
     * @param userId  ID владельца вещи, передается через заголовок запроса "X-Sharer-User-Id".
     * @return объект класса {@link ItemDto}.
     */
    @PostMapping
    public ItemDto addItemForUserWithId(@RequestHeader("X-Sharer-User-Id") @NonNull @Positive Long userId,
                                        @Validated(value = Create.class) @RequestBody ItemDto itemDto) {
        log.info("CONTROLLER: Запрос на добавление новой вещи: {} для пользователя с ID = {}.", itemDto, userId);
        return itemService.addItemForUserWithId(itemDto, userId);
    }

    /**
     * Метод обработки запроса на добавление комментария для вещи.
     *
     * @param commentDto объект класса {@link CommentDto}, передается через тело запроса.
     * @param userId     ID владельца вещи, передается через заголовок запроса "X-Sharer-User-Id".
     * @param itemId     ID вещи, передается через переменную пути.
     * @return объект класса {@link CommentDto}.
     */
    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Validated(value = Create.class) @RequestBody CommentDto commentDto,
                                 @Positive @PathVariable Long itemId) {
        log.info("Принят запрос на добавление комментария: {} для вещи с ID = {}.", commentDto, itemId);
        return itemService.addComment(userId, commentDto, itemId);
    }
    // PATCH запросы

    /**
     * Метод обработки запроса на обновление данных вещи.
     *
     * @param userId  ID владельца вещи, передается через заголовок запроса "X-Sharer-User-Id".
     * @param itemId  ID вещи, передается через переменную пути.
     * @param itemDto {@link ItemDto} с новыми значениями.
     * @return {@link ItemDto} обновленная информация о пользователе.
     */
    @PatchMapping("/{itemId}")
    public ItemDto updateItemForUserWithId(
            @RequestHeader("X-Sharer-User-Id") @NonNull @Positive Long userId,
            @PathVariable @Positive Long itemId, @Validated(value = Update.class) @RequestBody ItemDto itemDto) {
        log.info("CONTROLLER: Запрос на обновление вещи с ID = {} пользователя с ID = {}. {}", itemId, userId, itemDto);
        itemDto.setId(itemId);
        return itemService.updateItemForUserWithId(itemDto, userId);
    }

    // DELETE запросы

    /**
     * Метод обработки запроса на удаление вещи.
     *
     * @param userId ID пользователя, передается через заголовок запроса "X-Sharer-User-Id".
     * @param id     ID вещи, передается через переменную пути.
     */
    @DeleteMapping("/{id}")
    public void deleteItemForUserWithId(@RequestHeader("X-Sharer-User-Id") @NonNull @Positive Long userId,
                                        @PathVariable @Positive Long id) {
        log.info("CONTROLLER: Запрос на удаление вещи с ID = {}.", id);
        itemService.deleteItemForUserWithId(userId, id);
    }
}