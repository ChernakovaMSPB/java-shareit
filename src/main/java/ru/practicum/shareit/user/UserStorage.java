package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAllUsers();

    User getUserById(Long userId);

    User create(User user);

    User update(User user);

    void remove(Long userId);
}
