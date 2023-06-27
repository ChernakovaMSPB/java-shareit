package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Test
    public void createItemTest() {
        UserDto userDto = userMapper.toUserDto(new User(0L, "Name", "User@mail.ru"));
        Long userId = userService.create(userDto).getId();

        ItemDto itemDto = itemMapper.toItemDto(new Item(userId, "ItemName", "ItemDescription", true, null, userId));
        ItemDto itemDtoTest = itemService.create(userId, itemDto);

        assertThat("ItemName", equalTo(itemDtoTest.getName()));
        assertThat("ItemDescription", equalTo(itemDtoTest.getDescription()));
    }

    @Test
    public void updateItemNameTest() {
        UserDto userDto = userMapper.toUserDto(new User(0L, "Name", "User@mail.ru"));
        Long userId = userService.create(userDto).getId();

        ItemDto itemDto = itemMapper.toItemDto(new Item(0L, "ItemName", "ItemDescription", true, null, userId));
        Long itemId = itemService.create(userId, itemDto).getId();

        ItemDto itemDtoUpdate = itemMapper.toItemDto(new Item(itemId, "ItemNameUpdate", null, true, null, userId));
        itemDtoUpdate.setId(itemId);

        ItemDto itemDtoTest = itemService.update(userId, itemDtoUpdate);

        assertThat(itemId, equalTo(itemDtoTest.getId()));
        assertThat("ItemNameUpdate", equalTo(itemDtoTest.getName()));
    }

    @Test
    public void updateItemDescriptionTest() {
        UserDto userDto = userMapper.toUserDto(new User(0L, "Name", "User@mail.ru"));
        Long userId = userService.create(userDto).getId();

        ItemDto itemDto = itemMapper.toItemDto(new Item(0L, "ItemName", "ItemDescription", true, null, userId));
        Long itemId = itemService.create(userId, itemDto).getId();

        ItemDto itemDtoUpdate = itemMapper.toItemDto(new Item(itemId, "ItemName", "ItemDescriptionUpdate", true, null, userId));
        itemDtoUpdate.setId(itemId);
        ItemDto itemDtoTest = itemService.update(userId, itemDtoUpdate);

        assertThat(itemId, equalTo(itemDtoTest.getId()));
        assertThat("ItemDescriptionUpdate", equalTo(itemDtoTest.getDescription()));
    }

    @Test
    public void updateItemAvailableTest() {
        UserDto userDto = userMapper.toUserDto(new User(0L, "Name", "User@mail.ru"));
        Long userId = userService.create(userDto).getId();

        ItemDto itemDto = itemMapper.toItemDto(new Item(0L, "ItemName", "ItemDescription", true, null, userId));
        Long itemId = itemService.create(userId, itemDto).getId();

        ItemDto itemDtoUpdate = itemMapper.toItemDto(new Item(itemId, null, null, false, null, userId));
        ItemDto itemDtoTest = itemService.update(userId, itemDtoUpdate);

        assertThat(itemId, equalTo(itemDtoTest.getId()));
        assertThat(false, equalTo(itemDtoTest.getAvailable()));
    }

    @Test
    public void updateItemNotUserIdTest() {
        UserDto userDto = userMapper.toUserDto(new User(0L, "Name", "User@mail.ru"));
        Long userId = userService.create(userDto).getId();
        UserDto userDto1 = userMapper.toUserDto(new User(0L, "Name", "User1@mail.ru"));
        Long userId1 = userService.create(userDto1).getId();

        ItemDto itemDto = itemMapper.toItemDto(new Item(0L, "ItemName", "ItemDescription", true, null, userId));
        Long itemId = itemService.create(userId, itemDto).getId();
        itemDto.setId(itemId);

        assertThatThrownBy(() -> {
            itemService.update(userId1, itemDto);
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void searchTest() {
        List<ItemDto> items = itemService.searchItems("descrip", 0, 1);
        assertThat(0, equalTo(items.size()));
    }

    @Test
    public void itemTest() {
        User user = new User(1L, "Name", "user@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "Description", user, LocalDateTime.now());
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Name");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(itemRequest.getId());

        Item item1 = itemMapper.toItem(itemDto, user);

        assertThat(itemDto, equalTo(ItemMapper.toItemDto(item1)));
    }

    @Test
    public void searchPageTest() {
        List<ItemDto> items = itemService.searchItems("descrip", 0, 1);
        assertThat(0, equalTo(items.size()));
    }
}
