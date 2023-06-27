package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDataJpaTest {

    private final UserRepository repository;
    private User user;

    @BeforeEach
    public void setUpTest() {
        user = new User(1L, "Name", "Name@email.ru");
    }

    @Test
    public void createUserTest() {
        User userTest = repository.save(user);

        assertThat(userTest).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(userTest).hasFieldOrPropertyWithValue("name", "Name");
        assertThat(userTest).hasFieldOrPropertyWithValue("email", "Name@email.ru");
    }
}
