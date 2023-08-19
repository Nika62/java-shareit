package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateEmail;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
     public UserDto createUser(@Validated @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @Validated(UpdateEmail.class) @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable long userId) {
         userService.deleteUserById(userId);
    }

}
