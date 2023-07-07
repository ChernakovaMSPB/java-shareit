package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Component
public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemDto> items) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .requestor(UserMapper.toUserDto(itemRequest.getRequestor()))
                .created(itemRequest.getCreated())
                .description(itemRequest.getDescription())
                .items(items)
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .requestor(UserMapper.toUser(itemRequestDto.getRequestor()))
                .created(itemRequestDto.getCreated())
                .description(itemRequestDto.getDescription())
                .build();
    }
}
