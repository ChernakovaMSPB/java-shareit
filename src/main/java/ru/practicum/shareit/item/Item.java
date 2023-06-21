package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Entity(name = "Items")
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Item {
    @Id
    Long id;
    String name;
    String description;
    @Column(name = "is_available")
    Boolean available;
    @ManyToOne
    @JoinColumn(name = "users_id")
    User owner;
    @Column(name = "requests_id")
    Long request;

}
