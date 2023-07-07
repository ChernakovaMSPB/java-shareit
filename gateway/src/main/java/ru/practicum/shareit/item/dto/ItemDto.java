package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

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
public class ItemDto {
    Long id;
    @NotEmpty
    String name;
    @NotEmpty
    String description;
    @NotNull
    Boolean available;
    BookItemRequestDto lastBooking;
    BookItemRequestDto nextBooking;
    List<CommentDto> comments;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long requestId;

}
