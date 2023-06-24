package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.BagRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final BookingService bookingService;

    private Long itemId = Long.valueOf(1);
    private Long commentId = Long.valueOf(1);

    @Override
    public ItemDto create(Long ownerId, ItemDto item) {
        if (ownerId == null) {
            throw new BagRequestException("Id не указан");
        }
        Item createdItem = itemMapper.toItem(item, userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Некорректный владелец с ID")), null);
        createdItem.setId(itemId);
        ++itemId;
        itemRepository.save(createdItem);
        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public ItemDto update(Long userId, ItemDto item) {
        if (userId == null) {
            throw new BagRequestException("Id не указан");
        }
        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow(() -> new NotFoundException("Вещь не найдена с ID"));
        if (!updatedItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вещь не принадлежит этому владельцу");
        }
        if (item.getAvailable() != null) updatedItem.setAvailable(item.getAvailable());
        if (item.getDescription() != null) updatedItem.setDescription(item.getDescription());
        if (item.getName() != null) updatedItem.setName(item.getName());
        updatedItem = itemRepository.save(updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long requestorId) {
        if (itemId == null) {
            throw new BagRequestException("Id не указан");
        }
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена с ID"));
        return constructItemDtoForOwner(item, LocalDateTime.now(), requestorId);
    }

    @Override
    public List<ItemDto> getItemsByUser(Long userId) {
        if (userId == null) {
            throw new BagRequestException("Id не указан");
        }
        return itemRepository.findItemByOwnerId(userId)
                .stream()
                .map(s -> constructItemDtoForOwner(s, LocalDateTime.now(), userId))
                .sorted(Comparator.comparing(s -> s.getId()))
                .collect(toList());
    }

    @Override
    public List<ItemDto> searchItems(String search) {
        if (search.isEmpty()) {
            return new LinkedList<>();
        }
        return itemRepository.search(search)
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    public CommentDto create(CommentDto commentDto, Long itemId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        BookingDto bookingDto = bookingService.getUserBookings(Status.APPROVED.toString(), userId)
                .stream()
                .filter(s -> s.getItem().getId().equals(itemId))
                .min(Comparator.comparing(BookingDto::getEnd)).orElse(null);
        if (bookingDto != null && bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            Comment comment = new Comment();
            comment.setCreated(LocalDateTime.now());
            comment.setItem(itemRepository.findById(itemId).orElseThrow(null));
            comment.setAuthor(userRepository.findById(userId).orElseThrow(null));
            comment.setText(commentDto.getText());
            comment.setId(commentId);
            ++commentId;
            commentRepository.save(comment);
            return ItemMapper.toCommentDto(comment);
        } else {
            throw new BagRequestException("Пользователь не заказывал данную вещь");
        }
    }

    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findCommentsByItem_Id(itemId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(ItemMapper::toCommentDto)
                .collect(toList());
    }

    private ItemDto constructItemDtoForOwner(Item item, LocalDateTime now, Long userId) {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Booking lastBooking = bookingRepository
                .findAll()
                .stream()
                .filter(s -> s.getItem().getId().equals(item.getId()) && s.getStart().isBefore(now))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
        if (lastBooking != null && item.getOwner().getId() == userId) {
            itemDto.setLastBooking(BookingMapper.toBookingForItemDto(lastBooking));
        } else {
            itemDto.setLastBooking(null);
        }
        Booking nextBooking = bookingRepository
                .findAll()
                .stream()
                .filter(s -> s.getItem().getId().equals(item.getId()) && s.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);

        if (nextBooking != null && item.getOwner().getId().equals(userId) && nextBooking.getStatus().equals(Status.APPROVED)) {
            itemDto.setNextBooking(BookingMapper.toBookingForItemDto(nextBooking));
        } else {
            itemDto.setNextBooking(null);
        }
        itemDto.setComments(getCommentsByItemId(item.getId()));
        return itemDto;
    }
}
