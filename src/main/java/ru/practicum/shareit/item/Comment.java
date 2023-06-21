package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    Long id;
    String text;
    @ManyToOne()
    @JoinColumn(name = "items_id", referencedColumnName = "id")
    Item item;
    @ManyToOne()
    @JoinColumn(name = "users_id", referencedColumnName = "id")
    User author;
    LocalDateTime created;
}

