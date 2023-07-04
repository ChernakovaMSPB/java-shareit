package ru.practicum.shareit.user;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        userService = Mockito.mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void testGetAllUsers() {
        List<UserDto> users = new ArrayList<>();
        users.add(UserDto.builder().id(1L).name("John").email("john@example.com").build());
        users.add(UserDto.builder().id(2L).name("Jane").email("jane@example.com").build());
        Mockito.when(userService.getAllUsers()).thenReturn(users);

        List<UserDto> result = userController.getAllUsers();

        assertEquals(users, result);
        Mockito.verify(userService).getAllUsers();
    }

    @Test
    public void testGetUserById() {
        UserDto user = UserDto.builder().id(1L).name("John").email("john@example.com").build();
        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        UserDto result = userController.getUserById(1L);

        assertEquals(user, result);
        Mockito.verify(userService).getUserById(1L);
    }

    @Test
    public void testCreate() {
        UserDto user = UserDto.builder().id(null).name("John").email("john@example.com").build();
        UserDto createdUser = UserDto.builder().id(1L).name("John").email("john@example.com").build();
        Mockito.when(userService.create(user)).thenReturn(createdUser);

        UserDto result = userController.create(user);

        assertEquals(createdUser, result);
        Mockito.verify(userService).create(user);
    }

    @Test
    public void testUpdate() {
        UserDto user = UserDto.builder().id(1L).name("John").email("john@example.com").build();
        UserDto updatedUser = UserDto.builder().id(1L).name("John Doe").email("john.doe@example.com").build();
        Mockito.when(userService.update(1L, user)).thenReturn(updatedUser);

        UserDto result = userController.update(1L, user);

        assertEquals(updatedUser, result);
        Mockito.verify(userService).update(1L, user);
    }

    @Test
    public void testRemove() {
        userController.remove(1L);

        Mockito.verify(userService).remove(1L);
    }

}
