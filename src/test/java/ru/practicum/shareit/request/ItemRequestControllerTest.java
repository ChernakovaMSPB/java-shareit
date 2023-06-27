package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestController itemRequestController;

    private UserDto requestor = new UserDto(1L, "requestor", "request@mail.ru");
    private ItemDto requestedItem = new ItemDto(1L, "item", "desc", true,
            new BookingForItemDto(), new BookingForItemDto(), List.of(), 1L);
    private ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "desc 1",
            requestor, LocalDateTime.now(), List.of(requestedItem));

    @Test
    public void createItemRequestTest() throws Exception {
        itemRequestController.create(itemRequestDto1, requestor.getId());
        when(itemRequestController.getRequest(requestor.getId(), itemRequestDto1.getId()))
                .thenReturn(itemRequestDto1);

        mvc.perform(get("/requests/" + itemRequestDto1.getId())
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }
}
