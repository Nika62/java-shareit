package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class Item {
    @EqualsAndHashCode.Exclude
    private long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @NotBlank
    @Size(max = 250,message = "Длина описания не должна превышать 200 символов")
    private String description;
    @NotNull()
    private Boolean available;
    @NotNull(message = "Идентификатор пользователя должен быть заполнен")
    private long userId;
}
