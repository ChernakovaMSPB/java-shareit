package ru.practicum.shareit.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.BagRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = Arrays.asList(
                new User(1L, "John", "john@example.com"),
                new User(2L, "Jane", "jane@example.com")
        );
        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("John", result.get(0).getName());
        Assert.assertEquals("john@example.com", result.get(0).getEmail());
        Assert.assertEquals("Jane", result.get(1).getName());
        Assert.assertEquals("jane@example.com", result.get(1).getEmail());
    }

    @Test
    public void testGetUserById() {
        Long userId = 0L;
        User userExpected = new User();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userExpected));

        UserDto result = userService.getUserById(userId);
        UserDto expected = UserMapper.toUserDto(userExpected);

        assertEquals(expected, result);
    }

    @Test
    public void testGetUserById_NotFound() {
        Long userId = 0L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    public void testGetUserById_SuchElementException() {
        Long userId = 0L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> userService.getUserById(userId));
    }


    @Test
    public void testCreate() {
        UserDto userDto = new UserDto(null, "John", "john@example.com");
        User user = new User(1L, "John", "john@example.com");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        UserDto result = userService.create(userDto);

        Assert.assertEquals(1L, result.getId().longValue());
        Assert.assertEquals("John", result.getName());
        Assert.assertEquals("john@example.com", result.getEmail());
    }

    @Test(expected = BagRequestException.class)
    public void testCreate_InvalidData() {
        UserDto userDto = new UserDto(1L, null, null);

        userService.create(userDto);
    }

    @Test
    public void testUpdate_Success() {
        Long userId = 0L;
        User user = new User();
        UserDto userDto = new UserDto();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.update(userId, userDto);
        UserDto expected = UserMapper.toUserDto(user);

        assertEquals(expected, result);
     }

    @Test
    public void testUpdate_NotFoundException() {
        Long userId = 0L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    public void testUpdate_NullFields() {
        UserDto userDto = new UserDto(1L, null, null);
        User user = new User(1L, "John", "john@example.com");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.update(1L, userDto);
        });

        String expectedMessage = "[X] Пользователь с 1ID не существует";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
    }

    @Test
    public void testUpdate_EmptyFields() {
        UserDto userDto = new UserDto(1L, "John", "john@example.com");
        User user = new User(1L, "John", "john@example.com");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.update(1L, userDto);
        });

        String expectedMessage = "[X] Пользователь с 1ID не существует";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
    }

    @Test
    public void testRemove() {
        userService.remove(1L);
    }
}
