package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemMapperTest {

    @Test
    public void toItemDtoTest() {
        Item item = new Item(1L, "name", "descr",
                true, new User(), 0L);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(itemDto.getRequestId(), item.getRequest());
    }

    @Test
    public void toItemTest() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .requestId(0L)
                .build();

        Item item = ItemMapper.toItem(itemDto, owner);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(itemDto.getRequestId(), item.getRequest());

    }

    @Test
    public void toCommentDtoTest() {
        Comment comment = new Comment();
        comment.setCreated(LocalDateTime.now());
        comment.setText("Sample comment");
        comment.setItem(new Item());
        comment.setId(1L);
        comment.setAuthor(new User());

        CommentDto commentDto = ItemMapper.toCommentDto(comment);

        assertEquals(comment.getCreated(), commentDto.getCreated());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getItem().getId(), commentDto.getItemDto().getId());
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
    }

}
