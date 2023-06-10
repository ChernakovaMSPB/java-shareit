package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component("InMemoryItemStorage")
@Slf4j
public class InMemoryItemStorage implements ItemStorage {

    private Long id = Long.valueOf(1);
    private Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(id);
        ++id;
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        if (items.containsKey(item.getId())) {
            Item currentItem = items.get(item.getId());
            if (currentItem.getId().equals(item.getId())) {
                if (item.getOwner() == currentItem.getOwner()) {
                    if (item.getAvailable() != null) {
                        currentItem.setAvailable(item.getAvailable());
                    }
                    if (item.getDescription() != null) {
                        currentItem.setDescription(item.getDescription());
                    }
                    if (item.getOwner() != null) {
                        currentItem.setOwner(item.getOwner());
                    }
                    if (item.getRequest() != null) {
                        currentItem.setRequest(item.getRequest());
                    }
                    if (item.getName() != null) {
                        currentItem.setName(item.getName());
                    }
                    return currentItem;
                }
                throw new ForbiddenException("Пользователь с ID " + item.getOwner().getId() +
                        " не является владельцем вещи с ID " + item.getId());
            }
        }
        throw new NotFoundException("Вещь с ID " + item.getId() + " не существует");
    }

    @Override
    public Item getItemById(Long itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        }
        throw new NotFoundException("Вещь с ID " + itemId + " не найдена");
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        List<Item> userItems = new LinkedList<>();
        for (Item currentItem : items.values()) {
            if (currentItem.getOwner().getId().equals(userId)) {
                userItems.add(currentItem);
            }
        }
        return userItems;
    }

    @Override
    public List<Item> searchItems(String search) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(search)) {
            return new LinkedList<>();
        }
        List<Item> foundedItems = new LinkedList<>();
        for (Item currentItem: items.values()) {
            if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(currentItem.getName(), search) ||
                    StringUtils.containsIgnoreCase(currentItem.getDescription(), search)) {
                foundedItems.add(currentItem);
            }
        }
        return foundedItems;
    }
}
