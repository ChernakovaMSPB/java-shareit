package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemRequestMapperTest {

    @Test
    public void toModelTest() {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "desc 1",
                new UserDto(), LocalDateTime.now(), List.of(new ItemDto()));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto1);

        assertEquals(itemRequest.getId(), itemRequestDto1.getId());
        assertEquals(itemRequest.getRequestor(), UserMapper.toUser(itemRequestDto1.getRequestor()));
        assertEquals(itemRequest.getCreated(), itemRequestDto1.getCreated());
        assertEquals(itemRequest.getDescription(), itemRequestDto1.getDescription());

    }
}
