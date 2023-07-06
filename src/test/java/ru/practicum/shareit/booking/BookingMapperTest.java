package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BookingMapperTest {

    @Test
    public void testToBookingForItemDto() {
        Booking booking = new Booking();

        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2025, 01, 01, 01, 01));
        booking.setEnd(LocalDateTime.of(2025, 02, 01, 01, 01));

        User booker = new User();
        booker.setId(2L);
        booking.setBooker(booker);

        BookingForItemDto bookingDto = BookingMapper.toBookingForItemDto(booking);

        assertEquals(1, bookingDto.getId());
        assertEquals(LocalDateTime.of(2025, 01, 01, 01, 01), bookingDto.getStart());
        assertEquals(LocalDateTime.of(2025, 02, 01, 01, 01), bookingDto.getEnd());
        assertEquals(2, bookingDto.getBookerId());
    }

    @Test
    public void testToBookingDto() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2022, 1, 1, 12, 0));
        booking.setEnd(LocalDateTime.of(2022, 1, 1, 14, 0));
        booking.setItem(new Item());
        booking.setBooker(new User());
        booking.setStatus(Status.WAITING);

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        assertEquals(1L, bookingDto.getId());
        assertEquals(LocalDateTime.of(2022, 1, 1, 12, 0), bookingDto.getStart());
        assertEquals(LocalDateTime.of(2022, 1, 1, 14, 0), bookingDto.getEnd());
    }

    @Test
    void testToBookingFromDtoCreate() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate();
        bookingDtoCreate.setStart(LocalDateTime.of(2022, 1, 1, 12, 0));
        bookingDtoCreate.setEnd(LocalDateTime.of(2022, 1, 1, 14, 0));

        Long id = 1L;
        User booker = new User();
        Item item = new Item();

        Booking booking = BookingMapper.toBookingFromDtoCreate(bookingDtoCreate, id, booker, item);

        assertEquals(id, booking.getId());
        assertEquals(LocalDateTime.of(2022, 1, 1, 12, 0), booking.getStart());
        assertEquals(LocalDateTime.of(2022, 1, 1, 14, 0), booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(Status.WAITING, booking.getStatus());
    }
}