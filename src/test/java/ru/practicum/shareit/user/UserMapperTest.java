package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    private User user;

    @BeforeEach
    private void beforeEachTest() {
        user = new User(Long.valueOf(1), "user1", "user1@email.com");
    }

    @Test
    public void toDtoTest() {
        UserDto dto = UserMapper.toUserDto(user);

        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    public void toModelTest() {
        UserDto dto = new UserDto(1L, "user1", "user1@email.com");
        User fromDtoUser = UserMapper.toUser(dto);

        assertEquals(dto.getId(), fromDtoUser.getId());
        assertEquals(dto.getName(), fromDtoUser.getName());
        assertEquals(dto.getEmail(), fromDtoUser.getEmail());
    }
}
