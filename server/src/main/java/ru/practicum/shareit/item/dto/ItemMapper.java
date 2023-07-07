package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest())
                .build();
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .available(itemDto.getAvailable())
                .description(itemDto.getDescription())
                .owner(owner)
                .request(itemDto.getRequestId())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .created(comment.getCreated())
                .text(comment.getText())
                .itemDto(ItemMapper.toItemDto(comment.getItem()))
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .build();
    }

}
