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

        UserDto userDto = new UserDto(id, name, email);

        return userDto;
    }
}
