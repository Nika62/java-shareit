package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(long id, UserDto userDto);

    UserDto getUserById(long id);

    List<UserDto> getAllUsers();

    boolean deleteUserById(long id);
}
