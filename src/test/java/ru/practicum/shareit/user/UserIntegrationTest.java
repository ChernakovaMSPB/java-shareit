package ru.practicum.shareit.user;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class UserIntegrationTest {

    @Autowired
    private UserController userController;
    private final UserDto userDtoTestOne = new UserDto(0L, "TestName1", "test1@test.test");
    private final UserDto userDtoTestTwo = new UserDto(0L, "TestName2", "test2@test.test");

    @Test(expected = NullPointerException.class)
    public void whenCheckGetAllIdMethod() {
        UserDto userDtoOne = userController.create(userDtoTestOne);
        UserDto userDtoTwo = userController.create(userDtoTestTwo);

        List<UserDto> userDtos = userController.getAllUsers();
        assertEquals(2, userDtos.size());
        assertEquals(userDtoOne, userDtos.get(0));
        assertEquals(userDtoTwo, userDtos.get(1));
    }

    @Test(expected = NullPointerException.class)
    public void whenCheckGetIdMethod() {
        UserDto userDtoOne = userController.create(userDtoTestOne);
        UserDto userDtoTwo = userController.create(userDtoTestTwo);

        assertEquals(userDtoOne, userController.getUserById(1L));
        assertNotEquals(userDtoTwo, userController.getUserById(1L));
        assertEquals(userDtoTwo, userController.getUserById(2L));
        assertNotEquals(userDtoOne, userController.getUserById(2L));
    }

    @Test(expected = NullPointerException.class)
    public void whenCheckCreateMethod() {
        UserDto userDtoOne = userController.create(userDtoTestOne);
        UserDto userDtoTwo = userController.create(userDtoTestTwo);

        assertEquals(userDtoOne, userController.getUserById(1L));
        assertEquals("TestName1", userDtoOne.getName());
        assertEquals("test1@test.test", userDtoOne.getEmail());
        assertEquals(userDtoTwo, userController.getUserById(2L));
        assertEquals("TestName2", userDtoTwo.getName());
        assertEquals("test2@test.test", userDtoTwo.getEmail());
    }

    @Test(expected = NullPointerException.class)
    public void whenCheckUpdateMethod() {
        UserDto userDto = userController.create(userDtoTestOne);
        UserDto updateName = new UserDto();
        updateName.setName("update");
        UserDto updateEmail = new UserDto();
        updateEmail.setEmail("update@update.com");

        userController.update(1L, updateName);

        UserDto updateNameTest = userController.getUserById(1L);
        assertNotEquals(userDto, updateNameTest);
        assertEquals("update", updateNameTest.getName());

        userController.update(1L, updateEmail);

        UserDto updateEmailTest = userController.getUserById(1L);
        assertNotEquals(userDto, updateNameTest);
        assertEquals("update@update.com", updateEmailTest.getEmail());
    }

}
