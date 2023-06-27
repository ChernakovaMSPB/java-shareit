package ru.practicum.shareit.user;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    Gson gson = new Gson();

    @MockBean
    private UserController userController;
    private UserDto userDto1 = new UserDto(Long.valueOf(1), "user1", "user1@test.ru");
    private UserDto userDto2 = new UserDto(Long.valueOf(2), "user2", "user2@test.ru");
    private UserDto userDto2Update = new UserDto(Long.valueOf(2), "user2", "update2@test.ru");

    @Test
    public void getAllUsersTest() throws Exception {
        userController.create(userDto1);
        when(userController.getAllUsers())
                .thenReturn(List.of(userDto1));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(gson.toJson(List.of(userDto1))));

    }

    @Test
    public void getUserByIdTest() throws Exception {
        userController.create(userDto1);
        when(userController.getUserById(userDto1.getId()))
                .thenReturn(userDto1);

        mvc.perform(get("/users/" + userDto1.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(gson.toJson(userDto1)));
    }

    @Test
    public void createUserTest() throws Exception {
        userController.create(userDto2);
        when(userController.create(userDto2))
                .thenReturn(userDto2);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(userDto2)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(gson.toJson(userDto2)));
    }

    @Test
    public void updateUserTest() throws Exception {
        userController.create(userDto2);
        userController.update(userDto2.getId(), userDto2Update);

        when(userController.update(userDto2.getId(), userDto2Update))
                .thenReturn(userDto2Update);

        mvc.perform(patch("/users/" + userDto2.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(userDto2Update)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(gson.toJson(userDto2Update)));
    }

    @Test
    public void removeUserTest() throws Exception {
        userController.create(userDto1);
        userController.create(userDto2);

        when(userController.getAllUsers())
                .thenReturn(List.of(userDto1, userDto2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(gson.toJson(List.of(userDto1, userDto2))));

        mvc.perform(delete("/users/" + userDto1.getId().toString()))
                .andExpect(status().isOk());

        when(userController.getAllUsers())
                .thenReturn(List.of(userDto2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(gson.toJson(List.of(userDto2))));

    }
}
