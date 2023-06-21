package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader(USER_ID) Long bookerId,
                                 @Valid @RequestBody BookingDtoCreate bookingDtoCreate) {
        log.info("Получен запрос к эндпоинту: '/bookings' +" +
                " на создание бронирования от пользователя с ID={}", bookerId);
        return bookingService.create(bookingDtoCreate, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId,
                                 @RequestHeader(USER_ID) Long bookerId,
                                 @RequestParam Boolean approved) {
        return bookingService.update(bookingId, bookerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader(USER_ID) Long bookerId) {
        return bookingService.getBooking(bookingId, bookerId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestParam(defaultValue = "ALL") String state,
                                            @RequestHeader(USER_ID) Long userId) {
        return bookingService.getUserBookings(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getUserItemsBookings(@RequestParam(defaultValue = "ALL") String state,
                                                 @RequestHeader(USER_ID) Long userId) {
        return bookingService.getUserItemsBookings(state, userId);
    }

}