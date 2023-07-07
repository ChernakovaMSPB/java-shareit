package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated @RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader(value = "X-Sharer-User-Id") Long requesterId) {
        log.info("Создание запросов на вещи с Id: {} и пользователя с Id: {}", itemRequestDto, requesterId);
        return itemRequestClient.create(requesterId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@NotNull @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Получение запроса пользователя с Id: {}", requesterId);
        return itemRequestClient.getUserRequests(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUserRequests(@NotNull @RequestHeader("X-Sharer-User-Id") Long requesterId,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получение запроса от пользователя с Id: {} на получение всех запросов", requesterId);
        return itemRequestClient.getOtherUserRequests(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @NotNull @PathVariable Long requestId) {
        log.info("Получение запроса пользователя с Id: {}", userId);
        return itemRequestClient.getRequest(userId, requestId);
    }


}
