package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(User user);

    UserDto updateUser(long id, User user);

    UserDto getUserById(long id);

    List<UserDto> getAllUsers();

    boolean deleteUserById(long id);
}
