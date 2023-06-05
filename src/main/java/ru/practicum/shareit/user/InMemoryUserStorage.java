package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ExistingException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private Long uniqueUserId = Long.valueOf(1);
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        }
        throw new NotFoundException("Пользователь с ID " + userId + " не существует");
    }

    @Override
    public User create(User user) {
        emailExist(user.getId(), user.getEmail());
        user.setId(uniqueUserId);
        ++uniqueUserId;
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            User currentUser = users.get(user.getId());
            if (user.getEmail() != null) {
                emailExist(user.getId(), user.getEmail());
                currentUser.setEmail(user.getEmail());
            }
            if (user.getName() != null) currentUser.setName(user.getName());
            return currentUser;
        }
        throw new NotFoundException("Пользователь не существует");
    }

    @Override
    public void remove(Long userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
            return;
        }
        throw new NotFoundException("Пользователь не существует");
    }

    private boolean emailExist(Long userId, String email) {
        if (!users.isEmpty()) {
            if (users.values().stream().anyMatch(user -> user.getEmail().equals(email))) {
                if (users.get(userId) != null && users.get(userId).getEmail().equals(email)) {
                    return false;
                }
                log.trace("[X] Пользователь с email _{} уже существует", email);
                throw new ExistingException("Пользователь с таким email уже существует");
            }
        }
        return false;
    }
}
