package ru.practicum.shareit.booking;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "bookings")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Booking {
    @Id
    Long id;
    @Column(name = "start_date")
    LocalDateTime start;
    @Column(name = "end_date")
    LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "items_id")
    Item item;
    @ManyToOne
    @JoinColumn(name = "users_id")
    User booker;
    @Enumerated(EnumType.STRING)
    Status status;
}
