package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto item);

    ItemDto update(Long userId, ItemDto item);


    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemsByUser(Long userId);

    List<ItemDto> searchItems(String search);

}
