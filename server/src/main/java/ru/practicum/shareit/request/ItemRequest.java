package ru.practicum.shareit.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ItemRequest {
    @Id
    Long id;
    String description;
    @ManyToOne
    @JoinColumn(name = "users_id")
    User requestor;
    LocalDateTime created;

}
