package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;
    @Email(message = "Некорректный адрес электронной почты")
    @NotBlank(message = "Адрес электронной почты не может быть пустым")
    @Email(message = "Некорректный адрес электронной почты")
    private String email;

    public UserDto(String name) {
        this.name = name;
    }

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
