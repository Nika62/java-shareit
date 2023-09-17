package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.HelperCreationEntities.getUserDto;
import static ru.practicum.shareit.HelperCreationEntities.getUserDtoWithoutId;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceImplIntegrationTest {
    @Autowired
    private final UserService userService;

    @Test
    void shouldCreateUser() {
        UserDto userDto = userService.createUser(getUserDtoWithoutId());
        assertAll(
                () -> assertEquals(userDto.getId(), 1),
                () -> assertEquals(userDto.getName(), "user no Id"),
                () -> assertEquals(userDto.getEmail(), "usernoid@mail.com")
        );
    }

    @Test
    void shouldReturnExceptionCreateRepeatingUser() {
        userService.createUser(getUserDtoWithoutId());
        Exception e = Assertions.assertThrows(ObjectAlreadyExistsException.class,
                () -> {
                    userService.createUser(getUserDtoWithoutId());
                });

        assertEquals("Пользователь уже зарегистрирован в базе", e.getMessage());
    }

    @Test
    void updateUser() {
        userService.createUser(getUserDtoWithoutId());
        UserDto userDto = userService.updateUser(1, getUserDto());

        assertAll(
                () -> assertEquals(userDto.getId(), 1),
                () -> assertEquals(userDto.getName(), "user name"),
                () -> assertEquals(userDto.getEmail(), "user@mail.com")
        );
    }

    @Test
    void shouldReturnTrowUpdateUserEmailAlreadyExist() {
        UserDto userDto = userService.createUser(getUserDtoWithoutId());
        userService.createUser(new UserDto("update", "update@mail.com"));
        userDto.setEmail("update@mail.com");

        Exception e = Assertions.assertThrows(ObjectAlreadyExistsException.class,
                () -> {
                    userService.updateUser(1, userDto);
                });

        assertEquals("Пользователь c электронной почтой update@mail.com зарегистрирован в базе", e.getMessage());
    }

    @Test
    void shouldReturnTrowUpdateUserNotExist() {
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    userService.updateUser(1, getUserDto());
                });

        assertEquals("Пользователь с id = 1 не найден", e.getMessage());
    }

    @Test
    void getUserById() {
        userService.createUser(getUserDtoWithoutId());
        UserDto userDto = userService.getUserById(1);
        UserDto userForComparison = getUserDtoWithoutId();
        assertAll(
                () -> assertEquals(userDto.getId(), 1),
                () -> assertEquals(userDto.getName(), userForComparison.getName()),
                () -> assertEquals(userDto.getEmail(), userForComparison.getEmail())
        );
    }

    @Test
    void getAllUsers() {
        userService.createUser(getUserDtoWithoutId());
        userService.createUser(new UserDto("userDto", "userdto@mail.com"));
        List<UserDto> allUsers = userService.getAllUsers();
        assertAll(
                () -> assertEquals(allUsers.size(), 2),
                () -> assertEquals(allUsers.get(0).getId(), 1),
                () -> assertEquals(allUsers.get(1).getId(), 2),
                () -> assertEquals(allUsers.get(1).getName(), "userDto"),
                () -> assertEquals(allUsers.get(1).getEmail(), "userdto@mail.com")
        );

    }

    @Test
    void shouldEmptyList() {
        assertEquals(userService.getAllUsers().size(), 0);
    }

    @Test
    void deleteUserById() {
        userService.createUser(getUserDtoWithoutId());
        userService.deleteUserById(1);
        assertEquals(userService.getAllUsers().size(), 0);
    }
}