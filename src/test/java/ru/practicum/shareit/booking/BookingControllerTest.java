package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    public void testCreateBooking() throws Exception {
        Long bookerId = 1L;
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate();
        BookingDto bookingDto = new BookingDto();

        when(bookingService.create(bookingDtoCreate, bookerId)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookingDtoCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService).create(bookingDtoCreate, bookerId);
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUpdateBooking() throws Exception {
        Long bookingId = 1L;
        Long bookerId = 2L;
        Boolean approved = true;
        BookingDto bookingDto = new BookingDto();

        when(bookingService.update(bookingId, bookerId, approved)).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", bookerId)
                        .param("approved", approved.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService).update(bookingId, bookerId, approved);
    }

    @Test
    public void testGetBooking() throws Exception {
        Long bookingId = 1L;
        Long bookerId = 2L;
        BookingDto bookingDto = new BookingDto();

        when(bookingService.getBooking(bookingId, bookerId)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService).getBooking(bookingId, bookerId);
    }

    @Test
    public void testGetUserBookings() throws Exception {
        String state = "ALL";
        Long userId = 1L;
        int from = 0;
        int size = 20;
        List<BookingDto> bookingDtoList = Arrays.asList(new BookingDto(), new BookingDto());

        when(bookingService.getUserBookings(state, userId, from, size)).thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(bookingDtoList.size())));

        verify(bookingService).getUserBookings(state, userId, from, size);
    }

    @Test
    public void testGetUserItemsBookings() throws Exception {
        String state = "ALL";
        Long userId = 1L;
        int from = 0;
        int size = 20;
        List<BookingDto> bookingDtoList = Arrays.asList(new BookingDto(), new BookingDto());

        when(bookingService.getUserItemsBookings(state, userId, from, size)).thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(bookingDtoList.size())));

        verify(bookingService).getUserItemsBookings(state, userId, from, size);
    }
}
