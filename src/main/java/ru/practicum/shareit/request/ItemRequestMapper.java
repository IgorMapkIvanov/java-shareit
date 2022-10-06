package ru.practicum.shareit.request;

import ru.practicum.shareit.interfaces.MapperDTO;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

public class ItemRequestMapper implements MapperDTO<ItemRequest, ItemRequestDto> {
    @Override
    public ItemRequestDto toDto(ItemRequest itemRequest) {
        //TODO Sprint add-item-requests.
        return null;
    }

    @Override
    public ItemRequest fromDto(ItemRequestDto itemRequestDto) {
        // TODO Sprint add-item-requests.
        return null;
    }
}