package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {
    public UserDto convertUserToUserDto(User user) {
        if (user == null) {
            return null;
        }

        long id = 0L;
        String name = null;
        String email = null;

        id = user.getId();
        name = user.getName();
        email = user.getEmail();

        return new UserDto(id, name, email);
    }

    public User convertUserDtoToUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        long id = 0L;
        String name = null;
        String email = null;

        id = userDto.getId();
        name = userDto.getName();
        email = userDto.getEmail();

        return new User(id, name, email);
    }
}
