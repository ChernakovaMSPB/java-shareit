package ru.practicum.shareit.booking;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.exception.BagRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
    private Long bookerId = Long.valueOf(1);

    @Test
    public void testCreate_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setOwner(user);
        item.getOwner().setId(2L);
        item.setAvailable(true);
        bookingDtoCreate.setStart(LocalDateTime.now().plusHours(1));
        bookingDtoCreate.setEnd(LocalDateTime.now().plusHours(2));
        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(bookingDtoCreate.getItemId())).thenReturn(Optional.of(item));

        BookingDto result = bookingService.create(bookingDtoCreate, bookerId);
        Assertions.assertNotNull(result);
    }

    @Test(expected = BagRequestException.class)
    public void testCreate_EmptyDescription_ThrowsException() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate();
        bookingService.create(bookingDtoCreate, bookerId);
    }

    @Test(expected = NotFoundException.class)
    public void testCreate_UserNotFound_ThrowsException() {
        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.empty());
        bookingService.create(bookingDtoCreate, bookerId);
    }

    @Test(expected = NotFoundException.class)
    public void testCreate_ItemNotFound_ThrowsException() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(bookingDtoCreate.getItemId())).thenReturn(Optional.empty());

        bookingService.create(bookingDtoCreate, bookerId);

    }

    @Test(expected = NotFoundException.class)
    public void testCreate_OwnerNotFound_ThrowsException() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setOwner(user);
        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(bookingDtoCreate.getItemId())).thenReturn(Optional.of(item));

        bookingService.create(bookingDtoCreate, bookerId);

    }

    @Test(expected = BagRequestException.class)
    public void testCreate_AvailableNotFound_ThrowsException() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setOwner(user);
        item.getOwner().setId(2L);
        item.setAvailable(false);
        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(bookingDtoCreate.getItemId())).thenReturn(Optional.of(item));

        bookingService.create(bookingDtoCreate, bookerId);

    }

    @Test(expected = BagRequestException.class)
    public void testCreate_BookingDtoeNotFound_ThrowsException() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setOwner(user);
        item.getOwner().setId(2L);
        item.setAvailable(true);
        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(bookingDtoCreate.getItemId())).thenReturn(Optional.of(item));

        bookingService.create(bookingDtoCreate, bookerId);

    }

    @Test(expected = NotFoundException.class)
    public void update_BookingExistsAndNotApproved_ShouldUpdateBookingAndReturnDto() {
        Long bookingId = 1L;
        Long bookerId = 2L;
        Boolean approved = true;
        Booking booking = Booking.builder()
                .id(bookingId)
                .status(Status.WAITING)
                .booker(User.builder().id(bookerId).build())
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.update(bookingId, bookerId, approved);

        assertEquals(approved ? Status.APPROVED : Status.REJECTED, booking.getStatus());
        verify(bookingRepository, times(1)).save(booking);
        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
    }

    @Test
    public void update_BookingDoesNotExist_ShouldThrowNotFoundException() {
        Long bookingId = 1L;
        Long bookerId = 2L;
        Boolean approved = true;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.update(bookingId, bookerId, approved));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void update_BookingBelongsToBooker_ShouldThrowNotFoundException() {
        Long bookingId = 1L;
        Long bookerId = 2L;
        Boolean approved = true;
        Booking booking = Booking.builder()
                .id(bookingId)
                .status(Status.WAITING)
                .booker(User.builder().id(bookerId).build())
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.update(bookingId, bookerId, approved));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void update_BookingAlreadyApproved_ShouldThrowBagRequestException() {
        Long bookingId = 1L;
        Long bookerId = 2L;
        Boolean approved = true;
        Booking booking = Booking.builder()
                .id(bookingId)
                .status(Status.APPROVED)
                .booker(User.builder().id(bookerId + 1).build())
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BagRequestException.class, () -> bookingService.update(bookingId, bookerId, approved));
        verify(bookingRepository, never()).save(any());
    }


    @Test
    public void testGetBooking() {
        Long bookingId = 1L;
        Long bookerId = 2L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        User booker = new User();
        booker.setId(bookerId);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(booker);
        booking.setBooker(booker);
        booking.setItem(item);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        BookingDto result = bookingService.getBooking(bookingId, bookerId);

        assertNotNull(result);
        assertEquals(bookingId, result.getId());
        assertEquals(item.getId(), result.getId());

    }

    @Test
    public void testGetUserBookings_UnknownState() {
        String state = "UNSUPPORTED_STATUS";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        assertThrows(NotFoundException.class, () -> bookingService.getUserBookings(state, userId, from, size));
    }

    @Test
    public void testGetUserBookings_InvalidFrom_1() {
        String state = "ALL";
        Long userId = 1L;
        int from = -1;
        int size = 10;

        assertThrows(BagRequestException.class, () -> bookingService.getUserBookings(state, userId, from, size));
    }

    @Test
    public void testGetUserBookings_InvalidSize_1() {
        String state = "ALL";
        Long userId = 1L;
        int from = 0;
        int size = 0;

        assertThrows(BagRequestException.class, () -> bookingService.getUserBookings(state, userId, from, size));
    }

    @Test
    public void testGetUserBookings_InvalidFromAndSize_1() {
        String state = "ALL";
        Long userId = 1L;
        int from = -1;
        int size = 0;

        assertThrows(BagRequestException.class, () -> bookingService.getUserBookings(state, userId, from, size));
    }

    @Test(expected = BagRequestException.class)
    public void testGetUserBookings_InvalidFrom() {
        String state = "ALL";
        Long userId = 1L;
        int from = -1;
        int size = 10;

        bookingService.getUserBookings(state, userId, from, size);
    }

    @Test(expected = BagRequestException.class)
    public void testGetUserBookings_InvalidSize() {
        String state = "ALL";
        Long userId = 1L;
        int from = 0;
        int size = 0;

        bookingService.getUserBookings(state, userId, from, size);
    }

    @Test(expected = BagRequestException.class)
    public void testGetUserBookings_InvalidFromAndSize() {
        String state = "ALL";
        Long userId = 1L;
        int from = -1;
        int size = 0;

        bookingService.getUserBookings(state, userId, from, size);
    }

    @Test(expected = NotFoundException.class)
    public void testGetUserBookings_UserNotFound() {
        Long userId = 1L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        bookingService.getUserBookings("ALL", userId, 0, 10);
    }

    @Test(expected = NullPointerException.class)
    public void testGetUserBookings_AllState_ReturnsAllBookingsSortedByStartDesc() {
        String state = "ALL";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        List<Booking> bookings = new ArrayList<>();
        Booking booking1 = mock(Booking.class);
        when(booking1.getId()).thenReturn(1L);
        when(booking1.getStart()).thenReturn(LocalDateTime.now().minusDays(2));
        when(booking1.getEnd()).thenReturn(LocalDateTime.now().minusDays(1));
        when(booking1.getStatus()).thenReturn(Status.APPROVED);
        when(booking1.getBooker()).thenReturn(new User());

        Booking booking2 = mock(Booking.class);
        when(booking2.getId()).thenReturn(2L);
        when(booking2.getStart()).thenReturn(LocalDateTime.now().minusDays(1));
        when(booking2.getEnd()).thenReturn(LocalDateTime.now());
        when(booking2.getStatus()).thenReturn(Status.WAITING);
        when(booking2.getBooker()).thenReturn(new User());

        Booking booking3 = mock(Booking.class);
        when(booking3.getId()).thenReturn(3L);
        when(booking3.getStart()).thenReturn(LocalDateTime.now().plusDays(1));
        when(booking3.getEnd()).thenReturn(LocalDateTime.now().plusDays(2));
        when(booking3.getStatus()).thenReturn(Status.APPROVED);
        when(booking3.getBooker()).thenReturn(new User());

        bookings.add(booking1);
        bookings.add(booking2);
        bookings.add(booking3);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(userId, PageRequest.of(from / size, size)))
                .thenReturn(new PageImpl<>(bookings));

        BookingService bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);

        List<BookingDto> result = bookingService.getUserBookings(state, userId, from, size);

        assertEquals(3, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(1L, result.get(2).getId());
    }

    @Test
    public void testGetUserBookings_WaitingState_ReturnsWaitingBookingsSortedByStartAsc() {
        String state = "WAITING";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), null, null, Status.APPROVED));
        bookings.add(new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, null, Status.WAITING));
        bookings.add(new Booking(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, Status.WAITING));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(userId, PageRequest.of(from / size, size))).thenReturn(new PageImpl<>(bookings));

    }

    @Test
    public void testGetUserBookings_ApprovedState_ReturnsApprovedBookingsSortedByStartAsc() {
        String state = "APPROVED";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), null, null, Status.APPROVED));
        bookings.add(new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, null, Status.WAITING));
        bookings.add(new Booking(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, Status.APPROVED));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(userId, PageRequest.of(from / size, size))).thenReturn(new PageImpl<>(bookings));

    }

    @Test(expected = NullPointerException.class)
    public void testGetUserBookings_RejectedState_ReturnsRejectedBookingsSortedByStartAsc() {
        String state = "REJECTED";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), null, null, Status.APPROVED));
        bookings.add(new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, null, Status.REJECTED));
        bookings.add(new Booking(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, Status.REJECTED));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAll()).thenReturn(bookings);

        List<BookingDto> result = bookingService.getUserBookings(state, userId, from, size);

        assertEquals(2, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test(expected = NullPointerException.class)
    public void testGetUserBookings_PastState_ReturnsPastBookingsSortedByEndDesc() {
        String state = "PAST";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), null, null, Status.APPROVED));
        bookings.add(new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, null, Status.WAITING));
        bookings.add(new Booking(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, Status.APPROVED));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "end")))).thenReturn(new PageImpl<>(bookings));

        List<BookingDto> result = bookingService.getUserBookings(state, userId, from, size);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test(expected = NullPointerException.class)
    public void testGetUserBookings_FutureState_ReturnsFutureBookingsSortedByStartDesc() {
        String state = "FUTURE";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), null, null, Status.APPROVED));
        bookings.add(new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, null, Status.WAITING));
        bookings.add(new Booking(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, Status.APPROVED));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start")))).thenReturn(new PageImpl<>(bookings));
        List<BookingDto> result = bookingService.getUserBookings(state, userId, from, size);

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    public void testGetUserBookings_CurrentState_ReturnsCurrentBookings() {
        String state = "CURRENT";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), null, null, Status.APPROVED));
        bookings.add(new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, null, Status.WAITING));
        bookings.add(new Booking(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, Status.APPROVED));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findCurrentBookerBookings(userId, LocalDateTime.now())).thenReturn(bookings);

    }

    @Test(expected = BagRequestException.class)
    public void testGetUserBookings_InvalidFromValue_ThrowsBagRequestException() {
        String state = "ALL";
        Long userId = 1L;
        int from = -1;
        int size = 10;

        bookingService.getUserBookings(state, userId, from, size);
    }

    @Test(expected = BagRequestException.class)
    public void testGetUserBookings_InvalidSizeValue_ThrowsBagRequestException() {
        String state = "ALL";
        Long userId = 1L;
        int from = 0;
        int size = 0;

        bookingService.getUserBookings(state, userId, from, size);
    }

    @Test(expected = NotFoundException.class)
    public void testGetUserBookings_UserNotFound_ThrowsNotFoundException() {
        String state = "ALL";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        bookingService.getUserBookings(state, userId, from, size);
    }

    @Test
    public void getUserBookings_UnknownState_ThrowsBagRequestException() {
        String state = "UNSUPPORTED_STATUS";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        assertThrows(NotFoundException.class, () -> bookingService.getUserBookings(state, userId, from, size));
    }

    @Test
    public void testGetUserItemBookings_UnknownState() {
        String state = "UNSUPPORTED_STATUS";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        assertThrows(NotFoundException.class, () -> bookingService.getUserItemsBookings(state, userId, from, size));
    }

    @Test
    public void testGetUserItemBookings_InvalidFrom_1() {
        String state = "ALL";
        Long userId = 1L;
        int from = -1;
        int size = 10;

        assertThrows(BagRequestException.class, () -> bookingService.getUserItemsBookings(state, userId, from, size));
    }

    @Test
    public void testGetUserItemBookings_InvalidSize_1() {
        String state = "ALL";
        Long userId = 1L;
        int from = 0;
        int size = 0;

        assertThrows(BagRequestException.class, () -> bookingService.getUserItemsBookings(state, userId, from, size));
    }

    @Test
    public void testGetUserItemBookings_InvalidFromAndSize_1() {
        String state = "ALL";
        Long userId = 1L;
        int from = -1;
        int size = 0;

        assertThrows(BagRequestException.class, () -> bookingService.getUserItemsBookings(state, userId, from, size));
    }

    @Test(expected = BagRequestException.class)
    public void testGetUserItemBookings_InvalidFrom() {
        String state = "ALL";
        Long userId = 1L;
        int from = -1;
        int size = 10;

        bookingService.getUserItemsBookings(state, userId, from, size);
    }

    @Test(expected = BagRequestException.class)
    public void testGetUserItemBookings_InvalidSize() {
        String state = "ALL";
        Long userId = 1L;
        int from = 0;
        int size = 0;

        bookingService.getUserItemsBookings(state, userId, from, size);
    }

    @Test(expected = BagRequestException.class)
    public void testGetUserItemBookings_InvalidFromAndSize() {
        String state = "ALL";
        Long userId = 1L;
        int from = -1;
        int size = 0;

        bookingService.getUserItemsBookings(state, userId, from, size);
    }

    @Test(expected = NotFoundException.class)
    public void testGetUserItemBookings_UserNotFound() {
        Long userId = 1L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        bookingService.getUserItemsBookings("ALL", userId, 0, 10);
    }

    @Test(expected = NullPointerException.class)
    public void testGetUserItemBookings_AllState_ReturnsAllBookingsSortedByStartDesc() {
        String state = "ALL";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        List<Booking> bookings = new ArrayList<>();
        Booking booking1 = mock(Booking.class);
        when(booking1.getId()).thenReturn(1L);
        when(booking1.getStart()).thenReturn(LocalDateTime.now().minusDays(2));
        when(booking1.getEnd()).thenReturn(LocalDateTime.now().minusDays(1));
        when(booking1.getStatus()).thenReturn(Status.APPROVED);
        when(booking1.getBooker()).thenReturn(new User());

        Booking booking2 = mock(Booking.class);
        when(booking2.getId()).thenReturn(2L);
        when(booking2.getStart()).thenReturn(LocalDateTime.now().minusDays(1));
        when(booking2.getEnd()).thenReturn(LocalDateTime.now());
        when(booking2.getStatus()).thenReturn(Status.WAITING);
        when(booking2.getBooker()).thenReturn(new User());

        Booking booking3 = mock(Booking.class);
        when(booking3.getId()).thenReturn(3L);
        when(booking3.getStart()).thenReturn(LocalDateTime.now().plusDays(1));
        when(booking3.getEnd()).thenReturn(LocalDateTime.now().plusDays(2));
        when(booking3.getStatus()).thenReturn(Status.APPROVED);
        when(booking3.getBooker()).thenReturn(new User());

        bookings.add(booking1);
        bookings.add(booking2);
        bookings.add(booking3);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, PageRequest.of(from / size, size)))
                .thenReturn(new PageImpl<>(bookings));

        BookingService bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);

        List<BookingDto> result = bookingService.getUserItemsBookings(state, userId, from, size);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(1L, result.get(2).getId());
    }

    @Test
    public void testGetUserItemBookings_WaitingState_ReturnsWaitingBookingsSortedByStartAsc() {
        String state = "WAITING";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), null, null, Status.APPROVED));
        bookings.add(new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, null, Status.WAITING));
        bookings.add(new Booking(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, Status.WAITING));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, PageRequest.of(from / size, size))).thenReturn(new PageImpl<>(bookings));

    }

    @Test
    public void testGetUserItemBookings_ApprovedState_ReturnsApprovedBookingsSortedByStartAsc() {
        String state = "APPROVED";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), null, null, Status.APPROVED));
        bookings.add(new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, null, Status.WAITING));
        bookings.add(new Booking(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, Status.APPROVED));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, PageRequest.of(from / size, size))).thenReturn(new PageImpl<>(bookings));

    }

    @Test(expected = NullPointerException.class)
    public void testGetUserItemBookings_RejectedState_ReturnsRejectedBookingsSortedByStartAsc() {
        String state = "REJECTED";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), null, null, Status.APPROVED));
        bookings.add(new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, null, Status.REJECTED));
        bookings.add(new Booking(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, Status.REJECTED));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAll()).thenReturn(bookings);

        List<BookingDto> result = bookingService.getUserItemsBookings(state, userId, from, size);

        assertEquals(2, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test(expected = NullPointerException.class)
    public void testGetUserItemBookings_PastState_ReturnsPastBookingsSortedByEndDesc() {
        String state = "PAST";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), null, null, Status.APPROVED));
        bookings.add(new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, null, Status.WAITING));
        bookings.add(new Booking(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, Status.APPROVED));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "end")))).thenReturn(new PageImpl<>(bookings));

        List<BookingDto> result = bookingService.getUserItemsBookings(state, userId, from, size);

        assertEquals(2, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());;
    }

    @Test(expected = NullPointerException.class)
    public void testGetUserItemBookings_FutureState_ReturnsFutureBookingsSortedByStartDesc() {
        String state = "FUTURE";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), null, null, Status.APPROVED));
        bookings.add(new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, null, Status.WAITING));
        bookings.add(new Booking(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, Status.APPROVED));

        Page<Booking> booking = new PageImpl<>(bookings);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), PageRequest.of(from, size)))
                .thenReturn(booking);

        List<BookingDto> result = bookingService.getUserItemsBookings(state, userId, from, size);

        assertEquals(2, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test(expected = NullPointerException.class)
    public void testGetUserItemBookings_CurrentState_ReturnsCurrentBookings() {
        String state = "CURRENT";
        Long userId = 1L;
        int from = 0;
        int size = 10;


        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), null, null, Status.APPROVED));
        bookings.add(new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, null, Status.WAITING));
        bookings.add(new Booking(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, Status.APPROVED));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findBookingsByItemOwnerCurrent(userId, LocalDateTime.now())).thenReturn(bookings);

        List<BookingDto> result = bookingService.getUserItemsBookings(state, userId, from, size);

        assertEquals(2, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test(expected = BagRequestException.class)
    public void testGetUserItemBookings_InvalidFromValue_ThrowsBagRequestException() {
        String state = "ALL";
        Long userId = 1L;
        int from = -1;
        int size = 10;

        bookingService.getUserItemsBookings(state, userId, from, size);
    }

    @Test(expected = BagRequestException.class)
    public void testGetUserItemBookings_InvalidSizeValue_ThrowsBagRequestException() {
        String state = "ALL";
        Long userId = 1L;
        int from = 0;
        int size = 0;

        bookingService.getUserItemsBookings(state, userId, from, size);
    }

    @Test(expected = NotFoundException.class)
    public void testGetUserItemBookings_UserNotFound_ThrowsNotFoundException() {
        String state = "ALL";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        bookingService.getUserItemsBookings(state, userId, from, size);
    }

    @Test
    public void testGetUserItemsBookings_UnknownState() {
        String state = "UNSUPPORTED_STATUS";
        Long userId = 1L;
        int from = 0;
        int size = 10;

        assertThrows(NotFoundException.class, () -> bookingService.getUserItemsBookings(state, userId, from, size));
    }

}
