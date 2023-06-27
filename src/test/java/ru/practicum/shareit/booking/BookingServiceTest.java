package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.exception.BagRequestException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Test
    public void createBookingTest() {
        UserDto userDto1 = userMapper.toUserDto(new User(0L, "Name", "User@mail.ru"));
        Long userId1 = userService.create(userDto1).getId();
        UserDto userDto = userMapper.toUserDto(new User(0L, "Name", "User1@mail.ru"));
        Long userId = userService.create(userDto).getId();
        ItemDto itemDto = itemMapper.toItemDto(new Item(0L, "ItemName", "ItemDescription", true, null, userId));
        Long itemId = itemService.create(userId1, itemDto).getId();

        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate();
        bookingDtoCreate.setStart(LocalDateTime.now().plusSeconds(2));
        bookingDtoCreate.setEnd(LocalDateTime.now().plusSeconds(4));
        bookingDtoCreate.setItemId(itemId);

        BookingDto booking = bookingService.create(bookingDtoCreate, userId);

        assertThat(itemId, equalTo(booking.getItem().getId()));
        assertThat(userId, equalTo(booking.getBooker().getId()));
        assertThat(Status.WAITING, equalTo(booking.getStatus()));
    }

    @Test
    public void createBookingAvailableFalseTest() {
        UserDto userDto = userMapper.toUserDto(new User(0L, "Name", "User1@mail.ru"));
        Long userId = userService.create(userDto).getId();

        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate();
        bookingDtoCreate.setStart(LocalDateTime.now());
        bookingDtoCreate.setEnd(LocalDateTime.now().plusNanos(2));

        assertThatThrownBy(() -> {
            bookingService.create(bookingDtoCreate, userId);
        }).isInstanceOf(BagRequestException.class);
    }

    @Test
    public void createBookingNotUserTest() {
        UserDto userDto1 = userMapper.toUserDto(new User(0L, "Name", "User@mail.ru"));
        Long userId1 = userService.create(userDto1).getId();

        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate();
        bookingDtoCreate.setStart(LocalDateTime.now());
        bookingDtoCreate.setEnd(LocalDateTime.now().plusNanos(2));

        assertThatThrownBy(() -> {
            bookingService.create(bookingDtoCreate, userId1);
        }).isInstanceOf(BagRequestException.class);
    }


    @Test
    public void updateBookingTest() {
        UserDto userDto1 = userMapper.toUserDto(new User(0L, "Name", "User@mail.ru"));
        Long userId1 = userService.create(userDto1).getId();
        UserDto userDto = userMapper.toUserDto(new User(0L, "Name", "User1@mail.ru"));
        Long userId = userService.create(userDto).getId();
        ItemDto itemDto = itemMapper.toItemDto(new Item(0L, "ItemName", "ItemDescription",
                true, UserMapper.toUser(userDto1), userId));
        Long itemId = itemService.create(userId1, itemDto).getId();

        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate();
        bookingDtoCreate.setStart(LocalDateTime.now().plusSeconds(2));
        bookingDtoCreate.setEnd(LocalDateTime.now().plusSeconds(4));
        bookingDtoCreate.setItemId(itemId);

        long bookingId = bookingService.create(bookingDtoCreate, userId).getId();
        BookingDto booking = bookingService.update(bookingId, userId1, false);

        assertThat(Status.REJECTED, equalTo(booking.getStatus()));

    }

    @Test
    public void getByIdTest() {
        UserDto userDto1 = userMapper.toUserDto(new User(0L, "Name", "User@mail.ru"));
        Long userId1 = userService.create(userDto1).getId();
        UserDto userDto = userMapper.toUserDto(new User(0L, "Name", "User1@mail.ru"));
        Long userId = userService.create(userDto).getId();
        ItemDto itemDto = itemMapper.toItemDto(new Item(0L, "ItemName", "ItemDescription", true, null, userId));
        Long itemId = itemService.create(userId1, itemDto).getId();

        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate();
        bookingDtoCreate.setStart(LocalDateTime.now().plusSeconds(2));
        bookingDtoCreate.setEnd(LocalDateTime.now().plusSeconds(4));
        bookingDtoCreate.setItemId(itemId);

        BookingDto bookingResponseDto = bookingService.create(bookingDtoCreate, userId);
        Long bookingId = bookingResponseDto.getId();

        BookingDto booking = bookingService.getBooking(bookingId, userId);

        assertThat(userId, equalTo(booking.getBooker().getId()));
        assertThat(bookingId, equalTo(booking.getId()));
        assertThat(bookingResponseDto, equalTo(booking));
    }
}
