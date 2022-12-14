package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {
    List<ItemDtoWithBooking> getAllItemsForOwnerWithId(Long userId, PageRequest pageRequest);

    ItemDtoWithBooking getItemById(Long userId, Long itemId);

    ItemDto addItemForUserWithId(ItemDto itemDto, Long userId);

    ItemDto updateItemForUserWithId(ItemDto itemDto, Long userId);

    List<ItemDto> searchItemByText(String text, PageRequest pageRequest);

    CommentDto addComment(Long userId, CommentDto commentDto, Long itemId);
}