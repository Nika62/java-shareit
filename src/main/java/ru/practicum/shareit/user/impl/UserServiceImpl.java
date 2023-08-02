package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserEmailValidator;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserEmailValidator emailValidator;

    @Override
    public UserDto createUser(User user) {
        return userRepository.createUser(user);
    }

    @Override
    public UserDto updateUser(long id, User user) {
        checkUserEmailValid(id, user);
        return userRepository.updateUser(id, user);
    }

    @Override
    public UserDto getUserById(long id) {
        return userRepository.getUserById(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public boolean deleteUserById(long id) {
        return userRepository.deleteUserById(id);
    }

    private void checkUserEmailValid(long id, User user) {
        if (Objects.nonNull(user.getEmail())) {
            if (!emailValidator.validateUserEmail(user.getEmail())) {
                log.info("Невозможно обновить информацию о пользователе с id = {}, некорректный адрес электронной почты", id);
                throw new ValidationException("Некорректный адрес электронной почты");
            }
        }
    }

}
