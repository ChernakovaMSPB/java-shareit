package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BagRequestException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("Показаны все пользователи");
        return userStorage.getAllUsers()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        try {
            return userMapper.toUserDto(userStorage.getUserById(userId));
        } catch (NoSuchElementException e) {
            log.trace("[X] Пользователь с _{} ID не существует", userId);
        }
        throw new RuntimeException();
    }

    @Override
    public UserDto create(UserDto user) {
        if (user.getEmail() == null || user.getName() == null) {
            throw new BagRequestException("Неверные данные пользователя");
        }
        checkIsValid(user);
        User addedUser = userStorage.create(userMapper.toUser(user));
        log.debug("[V] Пользователь с ID _{} добавлен", user.getId());
        return userMapper.toUserDto(addedUser);
    }

    @Override
    public UserDto update(Long userId, UserDto user) {
        user.setId(userId);
        User updatedUser = userStorage.update(userMapper.toUser(user));
        log.debug("[V] Пользователь с ID _{} успешно обновлен", user.getId());
        return userMapper.toUserDto(updatedUser);

    }

    @Override
    public void remove(Long userId) {
        userStorage.remove(userId);
        log.debug("[V] Пользователь с ID _{} успешно удален", userId);
    }

    private boolean checkIsValid(UserDto user) {
        if (user.getEmail() != null && (user.getEmail().isEmpty() || user.getEmail().isBlank() || !user.getEmail().contains("@"))) {
            throw new BagRequestException("Неверные данные пользователя");
        }
        if (user.getName() != null && (user.getName().isBlank() || user.getName().isEmpty())) {
            throw new BagRequestException("Неверные данные пользователя");
        }
        return true;
    }

}

