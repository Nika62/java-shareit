package ru.practicum.shareit.request.dto;


import lombok.Data;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class RequestDto {
    private long id;
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 250, message = "Длина описания не должна превышать 200 символов")
    private String description;
    @FutureOrPresent
    private LocalDateTime created;
    private List<ItemRequestDto> items = new ArrayList<>();

}
