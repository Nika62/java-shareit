package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @EqualsAndHashCode.Exclude
    private long id;
    @EqualsAndHashCode.Exclude
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;
    @Email(message = "Некорректный адрес электронной почты")
    @NotBlank(message = "Адрес электронной почты не может быть пустым")
    private String email;

    public User(String name) {
        this.name = name;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
