package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {
    List<ItemDtoWithBooking> getAllItemsForOwnerWithId(Long userId);

    ItemDtoWithBooking getItemById(Long userId, Long itemId);

    ItemDto addItemForUserWithId(ItemDto itemDto, Long userId);

    ItemDto updateItemForUserWithId(ItemDto itemDto, Long userId);

    void deleteItemForUserWithId(Long userId, Long itemId);

    List<ItemDto> searchItemByText(String text);

    CommentDto addComment(Long userId, CommentDto commentDto, Long itemId);
}