package ru.practicum.shareit.request;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Test
    public void testCreate() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        Long userId = 1L;
        ItemRequestDto expectedResult = new ItemRequestDto();
        when(itemRequestService.create(itemRequestDto, userId)).thenReturn(expectedResult);

        ItemRequestDto result = itemRequestController.create(itemRequestDto, userId);

        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetUserRequests() {
        Long userId = 1L;
        List<ItemRequestDto> expectedResult = new ArrayList<>();
        when(itemRequestService.getUserRequests(userId)).thenReturn(expectedResult);

        List<ItemRequestDto> result = itemRequestController.getUserRequests(userId);

        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetOtherUserRequests() {
        Long userId = 1L;
        int from = 0;
        int size = 1;
        List<ItemRequestDto> expectedResult = new ArrayList<>();
        when(itemRequestService.getOtherUserRequests(userId, from, size)).thenReturn(expectedResult);

        List<ItemRequestDto> result = itemRequestController.getOtherUserRequests(userId, from, size);

        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetRequest() {
        Long userId = 1L;
        Long requestId = 1L;
        ItemRequestDto expectedResult = new ItemRequestDto();
        when(itemRequestService.getRequest(userId, requestId)).thenReturn(expectedResult);

        ItemRequestDto result = itemRequestController.getRequest(userId, requestId);

        assertEquals(expectedResult, result);
    }
}
