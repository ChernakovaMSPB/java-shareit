package ru.practicum.shareit.request;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.BagRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreate_Success() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test description");
        Long userId = 1L;
        User requestor = new User();
        requestor.setId(userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
        Mockito.when(itemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(new ItemRequest());
        Mockito.when(itemService.getItemsByRequestId(Mockito.anyLong())).thenReturn(new ArrayList<>());

        ItemRequestDto result = itemRequestService.create(itemRequestDto, userId);

        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(requestor.getId(), result.getRequestor().getId());
    }

    @Test
    public void testCreate_EmptyDescription_ThrowsException() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        Long userId = 1L;

        assertThrows(BagRequestException.class,
                () -> itemRequestService.create(itemRequestDto, userId));
    }

    @Test
    public void testCreate_UserNotFound_ThrowsException() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test description");
        Long userId = 1L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.create(itemRequestDto, userId));
    }

    @Test(expected = NullPointerException.class)
    public void testGetUserRequests_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(new ItemRequest());
        List<ItemDto> itemDtos = new ArrayList<>();
        itemDtos.add(new ItemDto());


        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findItemRequestByRequestorIdOrderByCreatedDesc(userId)).thenReturn(itemRequests);
        Mockito.when(itemService.getItemsByRequestId(Mockito.anyLong())).thenReturn(itemDtos);


        List<ItemRequestDto> result = itemRequestService.getUserRequests(userId);
        ItemRequestDto expected = ItemRequestMapper.toItemRequestDto(itemRequests.get(0),itemDtos);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expected, result.get(0));
    }

    @Test
    public void testGetUserRequests_UserNotFound_ThrowsException() {
        Long userId = 1L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getUserRequests(userId));
    }

    @Test
    public void testGetOtherUserRequests_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        int from = 0;
        int size = 10;
        List<ItemRequest> itemRequests = new ArrayList<>();
        ItemRequest itemRequest = new ItemRequest();
        User requestor = new User();
        requestor.setId(2L);
        itemRequest.setRequestor(requestor);
        itemRequests.add(itemRequest);
        Page<ItemRequest> page = new PageImpl<>(itemRequests);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(page);

        List<ItemRequestDto> result = itemRequestService.getOtherUserRequests(userId, from, size);

        assertNotNull(result);
        assertEquals(itemRequests.size(), result.size());
    }

    @Test
    public void testGetOtherUserRequests_UserNotFound_ThrowsException() {
        Long userId = 1L;
        int from = 0;
        int size = 10;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getOtherUserRequests(userId, from, size));
    }

    @Test
    public void testGetOtherUserRequests_InvalidParameters_ThrowsException() {
        Long userId = 1L;
        int from = -1;
        int size = 0;

        assertThrows(BagRequestException.class,
                () -> itemRequestService.getOtherUserRequests(userId, from, size));
    }

    @Test(expected = NullPointerException.class)
    public void testGetRequest_Success() {
        Long userId = 1L;
        Long requestId = 1L;
        User user = new User();
        user.setId(userId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemService.getItemsByRequestId(Mockito.anyLong())).thenReturn(new ArrayList<>());

        ItemRequestDto result = itemRequestService.getRequest(userId, requestId);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
    }

    @Test
    public void testGetRequest_UserNotFound_ThrowsException() {
        Long userId = 1L;
        Long requestId = 1L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequest(userId, requestId));
    }

    @Test
    public void testGetRequest_RequestNotFound_ThrowsException() {
        Long userId = 1L;
        Long requestId = 1L;
        User user = new User();
        user.setId(userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequest(userId, requestId));
    }

}
