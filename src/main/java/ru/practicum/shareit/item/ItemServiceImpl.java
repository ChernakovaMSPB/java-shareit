package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BagRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(Long ownerId, ItemDto item) {
        if (ownerId == null) {
            throw new BagRequestException("Id не указан");
        }
        Item createdItem = itemMapper.toItem(item, userStorage.getUserById(ownerId), null);
        itemStorage.create(createdItem);
        return itemMapper.toItemDto(createdItem);
    }

    @Override
    public ItemDto update(Long userId, ItemDto item) {
        if (userId == null) {
            throw new BagRequestException("Id не указан");
        }
        Item updatedItem = itemMapper.toItem(item, userStorage.getUserById(userId), null);
        updatedItem = itemStorage.update(updatedItem);
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        if (itemId == null) {
            throw new BagRequestException("Id не указан");
        }
        return itemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUser(Long userId) {
        if (userId == null) {
            throw new BagRequestException("Id не указан");
        }
        return itemStorage.getUserItems(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String search) {
        return itemStorage.searchItems(search)
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
