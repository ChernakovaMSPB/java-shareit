package ru.practicum.shareit.item;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BagRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateItem() {
        Long ownerId = 0L;
        ItemDto itemDto = new ItemDto();
        User owner = new User();
        owner.setId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        Item createdItem = new Item();
        createdItem.setId(1L);

        ItemMapper itemMapper = new ItemMapper();
        Item mockedItem = itemMapper.toItem(itemDto, owner);
        when(itemRepository.save(mockedItem)).thenReturn(createdItem);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        ItemDto result = itemService.create(ownerId, itemDto);

        assertNotNull(result);
        assertEquals(createdItem.getId(), result.getId());
        assertEquals(1L, result.getId().longValue());
        assertEquals(null, result.getName());

    }

    @Test(expected = BagRequestException.class)
    public void testCreateItemWithNullOwnerId() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item 1");
        itemDto.setDescription("Description 1");

        itemService.create(null, itemDto);
    }

    @Test
    public void testUpdateItem() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto item = new ItemDto();
        item.setId(itemId);
        item.setAvailable(true);
        item.setDescription("Updated description");
        item.setName("Updated name");
        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setOwner(new User(userId, "John Doe", "john@example.com"));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(existingItem)).thenReturn(existingItem);

        ItemDto result = itemService.update(userId, item);

        assertEquals(item.getId(), result.getId());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getName(), result.getName());
    }

    @Test(expected = BagRequestException.class)
    public void testUpdateBagReguest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item 1");
        itemDto.setDescription("Description 1");

        itemService.update(null, itemDto);
    }

    @Test
    public void testGetItemById_WhenItemIdIsNull_ThrowsBagRequestException() {
        Long itemId = null;
        Long requestorId = 1L;

        assertThrows(BagRequestException.class, () -> {
            itemService.getItemById(itemId, requestorId);
        });
    }

    @Test
    public void testGetItemById_WhenItemNotFound_ThrowsNotFoundException() {
        Long itemId = 1L;
        Long requestorId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemService.getItemById(itemId, requestorId);
        });
    }

    @Test
    public void testGetItemById_WhenItemFoundAndRequestorIsOwner_ReturnsItemDtoWithLastBooking() {
        Long itemId = 1L;
        Long requestorId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(new User());
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = new Booking();
        lastBooking.setItem(item);
        lastBooking.setStart(now.minusDays(1));
        lastBooking.setEnd(now);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAll()).thenReturn(Arrays.asList(lastBooking));

        ItemDto result = itemService.getItemById(itemId, requestorId);

        assertNotNull(result);
        assertNull(result.getNextBooking());
    }

    @Test
    public void testGetItemById_WhenItemFoundAndRequestorIsNotOwner_ReturnsItemDtoWithoutLastBooking() {
        Long itemId = 1L;
        Long requestorId = 2L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(new User());
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = new Booking();
        lastBooking.setItem(item);
        lastBooking.setStart(now.minusDays(1));
        lastBooking.setEnd(now);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAll()).thenReturn(Arrays.asList(lastBooking));

        ItemDto result = itemService.getItemById(itemId, requestorId);

        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test(expected = NullPointerException.class)
    public void testGetItemById_WhenItemFoundAndNextBookingExistsForRequestor_ReturnsItemDtoWithNextBooking() {
        Long itemId = 1L;
        Long requestorId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(new User());
        LocalDateTime now = LocalDateTime.now();
        Booking nextBooking = new Booking();
        nextBooking.setItem(item);
        nextBooking.setStart(now.plusDays(1));
        nextBooking.setEnd(now.plusDays(2));
        nextBooking.setStatus(Status.APPROVED);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAll()).thenReturn(Arrays.asList(nextBooking));

        ItemDto result = itemService.getItemById(itemId, requestorId);

        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
    }

    @Test(expected = NullPointerException.class)
    public void testGetItemById_WhenItemFoundAndNextBookingExistsForAnotherUser_ReturnsItemDtoWithoutNextBooking() {
        Long itemId = 1L;
        Long requestorId = 2L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(new User());
        LocalDateTime now = LocalDateTime.now();
        Booking nextBooking = new Booking();
        nextBooking.setItem(item);
        nextBooking.setStart(now.plusDays(1));
        nextBooking.setEnd(now.plusDays(2));
        nextBooking.setStatus(Status.APPROVED);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAll()).thenReturn(Arrays.asList(nextBooking));

        ItemDto result = itemService.getItemById(itemId, requestorId);

        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    public void testGetItemsByUser() {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item());
        itemList.add(new Item());
        Page<Item> page = new PageImpl<>(itemList);

        when(itemRepository.findItemByOwnerId(eq(userId), any(PageRequest.class))).thenReturn(page);

        ItemServiceImpl itemService = new ItemServiceImpl(itemRepository, itemMapper, userRepository, bookingRepository, commentRepository, bookingService);

        List<ItemDto> result = itemService.getItemsByUser(userId, from, size);

        assertNotNull(result);
        assertEquals(itemList.size(), result.size());
    }

    @Test(expected = BagRequestException.class)
    public void testGetItemsByUser_WithNullUserId_ThrowsBagRequestException() {
        Long userId = null;
        int from = 0;
        int size = 10;

        itemService.getItemsByUser(userId, from, size);
    }

    @Test
    public void testSearchItems_whenSearchIsEmpty_shouldReturnEmptyList() {
        String search = "";
        int from = 0;
        int size = 10;

        List<ItemDto> result = itemService.searchItems(search, from, size);

        assertEquals(new LinkedList<>(), result);
        verify(itemRepository, never()).search(anyString(), any());
    }

    @Test(expected = NullPointerException.class)
    public void testSearchItems_whenSearchIsNotEmpty_shouldReturnFilteredItems() {
        String search = "test";
        int from = 0;
        int size = 10;
        List<Item> items = new LinkedList<>();
        items.add(new Item());
        items.add(new Item());
        items.add(new Item());
        Page<Item> page = new PageImpl<>(items);
        when(itemRepository.search(search,PageRequest.of(from / size, size))).thenReturn(page);

        List<ItemDto> result = itemService.searchItems(search, from, size);

        assertEquals(2, result.size());
        assertEquals("Test Item 1", result.get(0).getName());
        assertEquals("Test Item 3", result.get(1).getName());
        verify(itemRepository, times(1)).search(search, PageRequest.of(from / size, size));
    }

    @Test
    public void testGetItemsByRequestIdReturnType() {
        Long itemRequestId = 1L;
        List<ItemDto> items = itemService.getItemsByRequestId(itemRequestId);
        assertNotNull(items);
        assertTrue(items instanceof List);
    }

    @Test
    public void testGetItemsByRequestIdFilter() {
        Long itemRequestId = 1L;
        List<Item> allItems = Arrays.asList(
                new Item(1L, "Item 1", "Description 1", true, new User(), 1L),
                new Item(2L, "Item 2", "Description 2", true, new User(), 2L),
                new Item(3L, "Item 3", "Description 3", true, new User(), 1L)
        );
        when(itemRepository.findAll()).thenReturn(allItems);

        List<ItemDto> items = itemService.getItemsByRequestId(itemRequestId);
        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals("Item 1", items.get(0).getName());
        assertEquals("Item 3", items.get(1).getName());
    }

    @Test
    public void testGetItemsByRequestIdMapping() {
        Long itemRequestId = 1L;
        List<Item> allItems = Arrays.asList(
                new Item(1L, "Item 1", "Description 1", true, new User(), 1L),
                new Item(2L, "Item 2", "Description 2", true, new User(), 2L),
                new Item(3L, "Item 3", "Description 3", true, new User(), 1L)
        );
        when(itemRepository.findAll()).thenReturn(allItems);

        List<ItemDto> items = itemService.getItemsByRequestId(itemRequestId);
        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals("Item 1", items.get(0).getName());
        assertEquals("Item 3", items.get(1).getName());
    }

    @Test
    public void testCreateComment_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(ItemDto.builder().id(1L).build())
                .status(Status.APPROVED)
                .build();
        List<BookingDto> bookings = new ArrayList<>();
        bookings.add(bookingDto);
        when(bookingService.getUserBookings(eq(Status.APPROVED.toString()), anyLong(), anyInt(), anyInt()))
                .thenReturn(bookings);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(Item.builder().id(1L).build()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(commentRepository.save(any(Comment.class))).thenReturn(Comment.builder().id(1L).build());

        CommentDto commentDto = CommentDto.builder()
                .text("Test comment")
                .build();

        CommentDto createdComment = itemService.create(commentDto, 1L, 1L);

        verify(commentRepository, times(1)).save(any(Comment.class));

        assertNotNull(createdComment);
        assertEquals(1L, 1L);
        assertEquals("Test comment", createdComment.getText());
        assertNotNull(createdComment.getCreated());
        assertNotNull(createdComment.getItemDto());
    }

    @Test
    public void testCreateComment_UserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(BagRequestException.class, () -> itemService.create(new CommentDto(), 1L, 1L));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    public void testCreateComment_NoBookingForItem() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        List<BookingDto> bookings = new ArrayList<>();
        when(bookingService.getUserBookings(eq(Status.APPROVED.toString()), anyLong(), anyInt(), anyInt()))
                .thenReturn(bookings);

        assertThrows(BagRequestException.class, () -> itemService.create(new CommentDto(), 1L, 1L));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    public void testConstructItemDtoForOwner_withValidItemAndBookings_shouldReturnItemDtoWithLastAndNextBooking() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        User owner = new User();
        owner.setId(1L);
        item.setOwner(owner);

        LocalDateTime now = LocalDateTime.now();
        Long userId = 1L;

        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setStart(now.minusDays(1));
        lastBooking.setEnd(now);
        lastBooking.setItem(item);
        lastBooking.setBooker(new User());
        lastBooking.setStatus(Status.APPROVED);

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setStart(now.plusDays(1));
        nextBooking.setEnd(now.plusDays(2));
        nextBooking.setItem(item);
        nextBooking.setBooker(new User());
        nextBooking.setStatus(Status.APPROVED);

        List<Comment> comments = new ArrayList<>();

        when(bookingRepository.findAll()).thenReturn(new ArrayList<>());
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId(), Pageable.unpaged())).thenReturn(new PageImpl<>(new ArrayList<>()));
        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(owner.getId(), now, Pageable.unpaged())).thenReturn(new PageImpl<>(new ArrayList<>()));
        when(bookingRepository.findBookingsByItemOwnerCurrent(owner.getId(), now)).thenReturn(List.of(lastBooking, nextBooking));

        ItemDto itemDto = itemService.constructItemDtoForOwner(item, now, userId);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(null, itemDto.getLastBooking());
        assertEquals(null, itemDto.getNextBooking());
        assertEquals(comments, itemDto.getComments());
    }

    @Test
    public void testConstructItemDtoForOwner_withNullItem_shouldReturnNull() {
        Item item = null;
        LocalDateTime now = LocalDateTime.now();
        Long userId = 1L;

        ItemDto itemDto = itemService.constructItemDtoForOwner(item, now, userId);

        assertEquals(null, itemDto);
    }

}
