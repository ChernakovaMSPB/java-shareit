package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDtoCreate bookingDtoCreate, Long bookerId);

    BookingDto update(Long bookingId, Long bookerId, Boolean approved);

    BookingDto getBooking(Long bookingId, Long bookerId);

    List<BookingDto> getUserBookings(String state, Long userId, int from, int size);

    List<BookingDto> getUserItemsBookings(String state, Long userId, int from, int size);
}