package ru.practicum.shareit.item;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.Create;
import ru.practicum.shareit.interfaces.MapperDTO;
import ru.practicum.shareit.interfaces.Update;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

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
public class ItemController {
    private final ItemService<Item> itemService;
    private final MapperDTO<Item, ItemDto> itemMapper;

    // GET запросы.

    /**
     * Метод обработки запроса на получение списка всех вещей пользователя.
     *
     * @param userId ID пользователя, передается через заголовок запроса "X-Sharer-User-Id".
     * @return {@link List} содержащий {@link ItemDto}
     */
    @GetMapping
    public List<ItemDto> getAllItemsForOwnerWithId(@RequestHeader("X-Sharer-User-Id") @NonNull @Positive Long userId) {
        log.info("CONTROLLER: Запрос на получение списка с информацией всех вещей пользователя с ID = {}.", userId);
        return itemService.getAllItemsForOwnerWithId(userId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Метод обработки запроса на информации о вещи пользователя.
     *
     * @param userId ID пользователя, передается через заголовок запроса "X-Sharer-User-Id".
     * @param itemId ID вещи, передается через переменную пути.
     * @return {@link List} содержащий {@link ItemDto}
     */
    @GetMapping("/{itemId}")
    public ItemDto getItemByIdForOwnerWithId(
            @RequestHeader("X-Sharer-User-Id") @NonNull @Positive Long userId,
            @PathVariable @Positive(message = "ID вещи должено быть положительным.") Long itemId) {
        log.info("CONTROLLER: Запрос на получение информации о вещи с ID = {} пользователя с ID = {}.", itemId, userId);
        return itemMapper.toDto(itemService.getItemByIdForOwnerWithId(userId, itemId));
    }

    // POST запросы

    /**
     * Метод обработки запроса на добаление новой вещи польлзователя.
     *
     * @param itemDto объект класса {@link ItemDto}, передается через тело запроса.
     * @param userId  ID владельца вещи, передается через заголовок запроса "X-Sharer-User-Id".
     * @return объект класса {@link ItemDto}.
     */
    @PostMapping
    public ItemDto addItemForUserWithId(@RequestHeader("X-Sharer-User-Id") @NonNull @Positive Long userId,
                                        @Validated(value = Create.class) @RequestBody ItemDto itemDto) {
        log.info("CONTROLLER: Запрос на добавление новой вещи: {} для пользователя с ID = {}.", itemDto, userId);
        Item newItem = itemMapper.fromDto(itemDto);
        newItem.getOwner().setId(userId);
        Item addedItem = itemService.addItemForUserWithId(newItem);
        return itemMapper.toDto(addedItem);
    }

    // PATCH запросы

    /**
     * Метод обработки запроса на обновление данных вещи.
     *
     * @param userId  ID владельца вещи, передается через заголовок запроса "X-Sharer-User-Id".
     * @param id      ID вещи, передается через переменную пути.
     * @param itemDto {@link ItemDto} с новыми значениями.
     * @return {@link ItemDto} обновленная информация о пользователе.
     */
    @PatchMapping("/{id}")
    public ItemDto updateItemForUserWithId(
            @RequestHeader("X-Sharer-User-Id") @NonNull @Positive Long userId,
            @PathVariable @Positive Long id, @Validated(value = Update.class) @RequestBody ItemDto itemDto) {
        log.info("CONTROLLER: Запрос на обновление вещи с ID = {} пользователя с ID = {}. {}", id, userId, itemDto);
        itemDto.setId(id);
        Item updateItem = itemMapper.fromDto(itemDto);
        updateItem.getOwner().setId(userId);
        return itemMapper.toDto(itemService.updateItemForUserWithId(updateItem));
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