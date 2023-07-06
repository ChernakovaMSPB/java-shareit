package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    @Query("select b from bookings b " +
            "where b.booker.id = :bookerId and " +
            "b.start < :now and " +
            "b.end > :now " +
            "order by b.start desc")

    List<Booking> findCurrentBookerBookings(Long bookerId, LocalDateTime now);

    @Query("select b from bookings b " +
            "where b.item.owner.id = :userId " +
            "and b.start < :now " +
            "and b.end > :now " +
            "order by b.start asc")
    List<Booking> findBookingsByItemOwnerCurrent(Long userId, LocalDateTime now);
}
