package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto item);

    ItemDto update(Long userId, ItemDto item);

    ItemDto getItemById(Long itemId, Long requestorId);

    List<ItemDto> getItemsByUser(Long userId, int from, int size);

    List<ItemDto> searchItems(String search, int from, int size);

    List<ItemDto> getItemsByRequestId(Long itemRequestId);

    CommentDto create(CommentDto commentDto, Long itemId, Long userId);

}
