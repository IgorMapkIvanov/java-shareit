package ru.practicum.shareit.data;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static ru.practicum.shareit.data.UserAndUserDtoData.user1;
import static ru.practicum.shareit.data.UserAndUserDtoData.user2;

public class ItemAndItemDtoData {
    /**
     * {@link ru.practicum.shareit.item.model.Item Item 1}
     * <p>id = 1
     * <p>name = item1
     * <p>description = item1Description
     * <p>available = true
     * <P>owner = user1 {@link UserAndUserDtoData}
     * <P>requestId = O
     */
    public static Item item1 = Item
            .builder()
            .id(1L)
            .name("item1")
            .description("item1Description")
            .available(true)
            .owner(user1)
            .requestId(null)
            .build();
    /**
     * {@link ru.practicum.shareit.item.dto.ItemDto ItemDto 1}
     * <p>id = 1
     * <p>name = item1
     * <p>description = item1Description
     * <p>available = true
     * <P>owner = new {@link User}
     * <P>requestId = O
     */
    public static ItemDto itemDto1 = ItemDto
            .builder()
            .id(1L)
            .name("itemDto1")
            .description("itemDto1Description")
            .available(true)
            .owner(new User())
            .requestId(0L)
            .build();

    /**
     * {@link ru.practicum.shareit.item.model.Item Item 1}
     * <p>id = 1
     * <p>name = item1
     * <p>description = item1Description
     * <p>available = true
     * <P>owner = user1 {@link UserAndUserDtoData}
     * <P>requestId = O
     */
    public static Item item2 = Item
            .builder()
            .id(2L)
            .name("item2")
            .description("item2Description")
            .available(true)
            .owner(user2)
            .requestId(null)
            .build();
}
