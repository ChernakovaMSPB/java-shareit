package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingForItemDto toBookingForItemDto(Booking booking) {
        return BookingForItemDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Booking toBookingFromDtoCreate(BookingDtoCreate bookingDtoCreate, Long id, User booker, Item item) {
        return Booking.builder()
                .id(id)
                .start(bookingDtoCreate.getStart())
                .end(bookingDtoCreate.getEnd())
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
    }

}
