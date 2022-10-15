package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * Класс определяет с методами преобразования объекта класса <b>{@link Item}</b> в DTO класса <b>{@link ItemDto}</b> и наоборот.
 *
 * @author Igor Ivanov
 */
@Component
public class ItemMapper {
    /**
     * Метод преобразует объект класса {@link Item} в его DTO {@link ItemDto}.
     *
     * @param item объект класса
     * @return {@link ItemDto} - DTO объекта класса
     */
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .requestId(item.getRequestId())
                .build();
    }

    /**
     * Метод преобразует объект класса {@link Item} в его DTO {@link ItemDtoWithBooking}.
     *
     * @param item объект класса
     * @return {@link ItemDtoWithBooking} - DTO объекта класса
     */
    public static ItemDtoWithBooking toItemDtoWithBooking(Item item) {
        return ItemDtoWithBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    /**
     * Метод преобразования DTO объекта класса {@link ItemDto} в объект класса {@link Item}.
     *
     * @param itemDto DTO объекта класса
     * @return {@link Item} - объект класса
     */
    public static Item fromDto(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(new User())
                .requestId(itemDto.getRequestId())
                .build();
    }
}