package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService<T> {
    List<T> getAllItemsForOwnerWithId(Long userId);

    T getItemById(Long id);

    T addItemForUserWithId(T t);

    T updateItemForUserWithId(T t);

    void deleteItemForUserWithId(Long userId, Long id);

    List<Item> getItemSearchByNameAndDescription(String text);
}