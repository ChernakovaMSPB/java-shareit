package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    public void testCreateItem() throws Exception {
        Long ownerId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setId(1L);
        expectedItemDto.setName("Test Item");
        expectedItemDto.setDescription("Test Description");
        expectedItemDto.setAvailable(true);

        when(itemService.create(ownerId, itemDto)).thenReturn(expectedItemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Item")));

        verify(itemService, times(1)).create(ownerId, itemDto);
    }

    @Test
    public void testUpdateItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("Updated Item");

        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setId(itemId);
        expectedItemDto.setName("Updated Item");

        when(itemService.update(userId, itemDto)).thenReturn(expectedItemDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Item")));

        verify(itemService, times(1)).update(userId, itemDto);
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetItemById() throws Exception {
        Long itemId = 1L;
        Long requestorId = 1L;

        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setId(itemId);
        expectedItemDto.setName("Test Item");

        when(itemService.getItemById(itemId, requestorId)).thenReturn(expectedItemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", requestorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Item")));

        verify(itemService, times(1)).getItemById(itemId, requestorId);
    }

    @Test
    public void testGetItemsByUser() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 20;

        List<ItemDto> expectedItems = new ArrayList<>();
        expectedItems.add(new ItemDto(1L, "Item 1", "", true, null, null, null, null));
        expectedItems.add(new ItemDto(2L, "Item 2", "", true, null, null, null, null));

        when(itemService.getItemsByUser(userId, from, size)).thenReturn(expectedItems);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Item 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Item 2")));

        verify(itemService, times(1)).getItemsByUser(userId, from, size);
    }

    @Test
    public void testSearchItems() throws Exception {
        String text = "test";
        int from = 0;
        int size = 20;

        List<ItemDto> expectedItems = new ArrayList<>();
        expectedItems.add(new ItemDto(1L, "Item 1", "", true, null, null, null, null));
        expectedItems.add(new ItemDto(2L, "Item 2", "", true, null, null, null, null));

        when(itemService.searchItems(text, from, size)).thenReturn(expectedItems);

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Item 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Item 2")));

        verify(itemService, times(1)).searchItems(text, from, size);
    }

    @Test
    public void testCreateComment() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test Comment");

        CommentDto expectedCommentDto = new CommentDto();
        expectedCommentDto.setId(1L);
        expectedCommentDto.setText("Test Comment");

        when(itemService.create(commentDto, itemId, userId)).thenReturn(expectedCommentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Test Comment")));

        verify(itemService, times(1)).create(commentDto, itemId, userId);
    }
}