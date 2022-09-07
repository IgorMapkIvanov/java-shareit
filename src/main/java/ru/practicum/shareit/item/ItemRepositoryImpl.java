package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.WrongOwnerOfItemExceptions;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository<Item> {
    private Long id = 0L;
    private final Map<Long, Item> itemMap;

    @Override
    public List<Item> getAllItemsForOwnerWithId(Long userId) {
        return itemMap.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemByIdForOwnerWithId(Long userId, Long id) {
        Item item = itemMap.get(id);
        if (item.getOwner().getId().equals(userId)) {
            return item;
        } else {
            throw new WrongOwnerOfItemExceptions("Не корректный владелец вещи.");
        }
    }

    @Override
    public Item addItemForUserWithId(Item item) {
        item.setId(++id);
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItemForUserWithId(Item item) {
        Item itemUpdate = itemMap.get(item.getId());
        if (item.getOwner().getId().equals(itemUpdate.getOwner().getId())) {
            if ((item.getName() != null) && (!item.getName().equals(itemUpdate.getName()))) {
                itemUpdate.setName(item.getName());
            }
            if ((item.getDescription() != null) && (!item.getDescription().equals(itemUpdate.getDescription()))) {
                itemUpdate.setDescription(item.getDescription());
            }
            if ((item.getAvailable() != null) && (!item.getAvailable().equals(itemUpdate.getAvailable()))) {
                itemUpdate.setAvailable(item.getAvailable());
            }
            if ((item.getRequest() != null) && (!item.getRequest().equals(itemUpdate.getRequest()))) {
                itemUpdate.setRequest(item.getRequest());
            }
            itemMap.put(itemUpdate.getId(), itemUpdate);
            return itemUpdate;
        } else {
            throw new WrongOwnerOfItemExceptions("Вещь с ID = " + item.getId() + " не пренадлежит пользователю с ID = " +
                    item.getOwner().getId() + ".");
        }
    }

    @Override
    public void deleteItemForUserWithId(Long userId, Long id) {
        if (itemMap.get(id).getOwner().getId().equals(userId)) {
            itemMap.remove(id);
        } else {
            throw new WrongOwnerOfItemExceptions("Не корректный владелец вещи.");
        }
    }
}