package ru.practicum.shareit.item;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    Gson gson = new Gson();

    @MockBean
    private ItemController itemController;

    @Test
    public void createItemTest() throws Exception {
        ItemDto itemToAdd = new ItemDto();
        itemToAdd.setName("item1");
        itemToAdd.setDescription("desc 1");
        itemToAdd.setAvailable(true);

        itemController.create(1L, itemToAdd);
        when(itemController.create(anyLong(), any()))
                .thenReturn(itemToAdd);

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(itemToAdd))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(gson.toJson(itemToAdd)));
    }

    @Test
    public void updateItemTest() throws Exception {
        ItemDto itemToAdd = new ItemDto();
        itemToAdd.setName("item1");
        itemToAdd.setDescription("desc 1");
        itemToAdd.setAvailable(true);

        itemController.create(1L, itemToAdd);

        itemToAdd.setName("updatedName");
        itemController.update(1L, itemToAdd, 1L);
        when(itemController.update(anyLong(), any(), anyLong()))
                .thenReturn(itemToAdd);

        mvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(itemToAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemToAdd.getName()));
    }

    @Test
    public void getItemByIdTest() throws Exception {
        ItemDto itemToAdd = new ItemDto();
        itemToAdd.setName("item1");
        itemToAdd.setDescription("desc 1");
        itemToAdd.setAvailable(true);
        itemController.create(1L, itemToAdd);

        itemController.getItemById(itemToAdd.getId(), 1L);
        when(itemController.getItemById(anyLong(), anyLong()))
                .thenReturn(itemToAdd);

        mvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getItemsByUserTest() throws Exception {
        ItemDto itemToAdd = new ItemDto();
        itemToAdd.setName("item1");
        itemToAdd.setDescription("desc 1");
        itemToAdd.setAvailable(true);
        itemController.create(1L, itemToAdd);

        itemController.getItemsByUser(1L, 0, 1);
        when(itemController.getItemsByUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemToAdd));

        mvc.perform(MockMvcRequestBuilders.get("/items/")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(gson.toJson(List.of(itemToAdd))));
    }

    @Test
    public void searchItemsTest() throws Exception {
        itemController.searchItems("search", 0, 1);
        when(itemController.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/items/search?text=search")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(gson.toJson(List.of())));
    }

    @Test
    public void createComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        itemController.create(commentDto, 1L, 1L);
        when(itemController.create(any(), anyLong(), anyLong()))
                .thenReturn(commentDto);

        mvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
