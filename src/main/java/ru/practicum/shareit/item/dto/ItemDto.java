package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingLastNextDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = { "userId" })
public class ItemDto {
    private long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 250,message = "Длина описания не должна превышать 200 символов")
    private String description;
    @NotNull(message = "Статус не может быть пустым")
    private Boolean available;
    private UserDto user;
    private List<CommentDto> comments = new ArrayList<>();
    private BookingLastNextDto lastBooking;
    private BookingLastNextDto nextBooking;

}
