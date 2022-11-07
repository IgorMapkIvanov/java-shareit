package ru.practicum.shareit.Item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.data.ItemAndItemDtoData.item1;
import static ru.practicum.shareit.data.ItemAndItemDtoData.itemDto1;

class ItemMapperTest {
    @Test
    void shouldItemToItemDtoTest() {
        ItemDto itemDto = ItemMapper.toItemDto(item1);

        assertNotNull(itemDto);
        assertEquals(1, itemDto.getId());
        assertEquals("item1", itemDto.getName());
        assertEquals("item1Description", itemDto.getDescription());
        assertEquals(item1.getOwner(), itemDto.getOwner());
        assertEquals(true, itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void shouldItemToItemDtoWithBookingTest() {
        ItemDtoWithBooking itemDtoWithBooking = ItemMapper.toItemDtoWithBooking(item1);

        assertNotNull(itemDtoWithBooking);
        assertEquals(1, itemDtoWithBooking.getId());
        assertEquals("item1", itemDtoWithBooking.getName());
        assertEquals("item1Description", itemDtoWithBooking.getDescription());
        assertEquals(true, itemDtoWithBooking.getAvailable());
    }

    @Test
    void shouldItemDtoToItemTest() {
        Item item = ItemMapper.fromDto(itemDto1);

        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("itemDto1", item.getName());
        assertEquals("itemDto1Description", item.getDescription());
        assertEquals(itemDto1.getOwner().getId(), item.getOwner().getId());
        assertEquals(itemDto1.getOwner().getName(), item.getOwner().getName());
        assertEquals(itemDto1.getOwner().getEmail(), item.getOwner().getEmail());
        assertEquals(true, item.getAvailable());
        assertEquals(0L, item.getRequestId());
    }
}