package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
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

