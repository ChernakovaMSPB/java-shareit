package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    UserDto create(UserDto user);

    UserDto update(Long userId, UserDto user);

    void remove(Long userId);

}
