package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingForItemDto lastBooking;
    BookingForItemDto nextBooking;
    List<CommentDto> comments;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long requestId;

}
