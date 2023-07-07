package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BagRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private Long userId = Long.valueOf(1);

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("Показаны все пользователи");
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        try {
            return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow());
        } catch (NoSuchElementException e) {
            log.trace("[X] Пользователь с _{} ID не существует", userId);
        }
        throw new NotFoundException("[X] Пользователь с " + userId + "ID не существует");
    }

    @Override
    public UserDto create(UserDto user) {
        if (user.getEmail() == null || user.getName() == null) {
            throw new BagRequestException("Неверные данные пользователя");
        }
        checkIsValid(user);
        user.setId(userId);
        ++userId;
        User addedUser = userMapper.toUser(user);
        userRepository.save(addedUser);
        log.debug("[V] Пользователь с ID _{} добавлен", user.getId());
        return userMapper.toUserDto(addedUser);
    }

    @Override
    public UserDto update(Long userId, UserDto user) {
        checkIsValid(user);
//        User updatedUser = userRepository.findById(userId).orElseThrow(() ->
//                new NotFoundException("[X] Пользователь с " + userId + "ID не существует")
//        );
        User updatedUser = userRepository.findById(userId).orElseThrow();
        if (user.getName() != null) updatedUser.setName(user.getName());
        if (user.getEmail() != null) updatedUser.setEmail(user.getEmail());
        userRepository.save(updatedUser);
        log.debug("[V] Пользователь с ID _{} успешно обновлен", user.getId());
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public void remove(Long userId) {
        userRepository.deleteById(userId);
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

