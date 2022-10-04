package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService<Item> {
    private final ItemRepository<Item> itemRepository;
    private final UserRepository<User> userRepository;

    @Override
    public List<Item> getAllItemsForOwnerWithId(Long userId) {
        return itemRepository.getAllItemsForOwnerWithId(userRepository.getById(userId).orElseThrow().getId());
    }

    @Override
    public Item getItemByIdForOwnerWithId(Long userId, Long id) {
        return itemRepository.getItemByIdForOwnerWithId(userRepository.getById(userId).orElseThrow().getId(), id);
    }

    @Override
    public List<Item> getItemSearchByNameAndDescription(String text) {
        return text.equals("") ? Collections.emptyList() : itemRepository.getItemSearchByNameAndDescription(text.toLowerCase());
    }

    @Override
    public Item addItemForUserWithId(Item item) {
        item.setOwner(userRepository.getById(item.getOwner().getId()).orElseThrow());
        return itemRepository.addItemForUserWithId(item);
    }

    @Override
    public Item updateItemForUserWithId(Item item) {
        userRepository.getById(item.getOwner().getId()).orElseThrow();
        return itemRepository.updateItemForUserWithId(item);
    }

    @Override
    public void deleteItemForUserWithId(Long userId, Long id) {
        itemRepository.deleteItemForUserWithId(userId, id);
    }
}