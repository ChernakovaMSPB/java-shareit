package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.BagRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private Long bookingId = Long.valueOf(1);

    @Override
    public BookingDto create(BookingDtoCreate bookingDtoCreate, Long bookerId) {
        if (bookingDtoCreate.getItemId() == null || bookingDtoCreate.getStart() == null || bookingDtoCreate.getEnd() == null || bookingDtoCreate.getStart().equals(bookingDtoCreate.getEnd())) {
            throw new BagRequestException("Вещь не задана или не действительна");
        }
        User user = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + bookerId + " не найден"));
        Item item = itemRepository.findById(bookingDtoCreate.getItemId()).orElseThrow(() -> new NotFoundException("Вещь с ID " + bookingDtoCreate.getItemId() + " не найдена"));
        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Вещь с ID " + item.getId() + " принадлежит " + "пользователю с ID" + bookerId);
        }
        if (!item.getAvailable()) {
            throw new BagRequestException("Вещь с ID " + item.getId() + " не доступна");
        }
        if (bookingDtoCreate.getEnd().isBefore(LocalDateTime.now()) || bookingDtoCreate.getEnd().isBefore(bookingDtoCreate.getStart()) || bookingDtoCreate.getStart().isBefore(LocalDateTime.now())) {
            throw new BagRequestException("Начальное и конечное дата и время не действительны");
        }
        Booking booking = BookingMapper.toBookingFromDtoCreate(bookingDtoCreate, bookingId, user, item);
        booking.setId(bookingId);
        bookingRepository.save(booking);
        ++bookingId;
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto update(Long bookingId, Long bookerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с ID " + bookingId + " не найдено"));
        if (booking.getBooker().getId().equals(bookerId)) {
            throw new NotFoundException("Это ваша вещь");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BagRequestException("Бронирование одобрено");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long bookerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с ID " + bookingId + " не найдено"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException("Вещь с ID " + booking.getItem().getId() + " не найдена"));
        if (!booking.getBooker().getId().equals(bookerId) && !item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Пользователь с ID " + bookerId + " не имеет прав" + " чтобы оформить бронирование с ID " + bookingId);
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(String state, Long userId, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new BagRequestException("Неверные значения параметров");
        }
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        Sort sort = Sort.by("start").descending();
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBooker_IdOrderByStartDesc(userId, PageRequest.of(from / size, size))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "WAITING":
            case "APPROVED":
            case "REJECTED":
                return bookingRepository.findAll()
                        .stream()
                        .sorted((a, b) -> a.getStart().isBefore(b.getStart()) ? 1 : -1)
                        .filter(s -> s.getBooker().getId().equals(userId))
                        .filter(s -> s.getStatus().toString().equals(state))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), PageRequest.of(from / size, size, sort))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), PageRequest.of(from / size, size, sort))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findCurrentBookerBookings(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new BagRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> getUserItemsBookings(String state, Long userId, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new BagRequestException("Неверные значения параметров");
        }
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        Sort sort = Sort.by("start").descending();
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, PageRequest.of(from / size, size))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "WAITING":
            case "APPROVED":
            case "REJECTED":
                return bookingRepository.findAll()
                        .stream()
                        .sorted((a, b) -> a.getStart().isBefore(b.getStart()) ? 1 : -1)
                        .filter(s -> s.getItem().getOwner().getId().equals(userId))
                        .filter(s -> s.getStatus().toString().equals(state))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), PageRequest.of(from / size, size, sort))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(), PageRequest.of(from / size, size, sort))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findBookingsByItemOwnerCurrent(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new BagRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

    }
}
