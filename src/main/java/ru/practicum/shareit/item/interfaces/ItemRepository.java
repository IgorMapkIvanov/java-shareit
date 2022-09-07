package ru.practicum.shareit.item.interfaces;

import java.util.List;

public interface ItemRepository<T> {
    List<T> getAllItemsForOwnerWithId(Long userId);

    T getItemByIdForOwnerWithId(Long userId, Long id);

    T addItemForUserWithId(T t);

    T updateItemForUserWithId(T t);

    void deleteItemForUserWithId(Long userId, Long id);
}
