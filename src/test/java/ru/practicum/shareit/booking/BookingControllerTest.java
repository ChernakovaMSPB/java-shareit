package ru.practicum.shareit.booking;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    Gson gson = new Gson();

    @MockBean
    private BookingController bookingController;
    private BookingDtoCreate bookingDtoCreate;

    @BeforeEach
    public void setUpTest() {
        bookingDtoCreate = new BookingDtoCreate();
        bookingDtoCreate.setStart(LocalDateTime.now().plusSeconds(2));
        bookingDtoCreate.setEnd(LocalDateTime.now().plusSeconds(4));
        bookingDtoCreate.setItemId(1L);
    }

    @Test
    public void addingBookingWithNonExistUserTest() throws Exception {
        Booking booking = BookingMapper.toBookingFromDtoCreate(bookingDtoCreate, 1L, new User(), new Item());
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        when(bookingController.create(anyLong(), any())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(gson.toJson(bookingDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isBadRequest());
    }
}
