package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user;
        try {
        user = userRepository.save(userMapper.convertUserDtoToUser(userDto));

        } catch(DataIntegrityViolationException e) {
            throw new ObjectAlreadyExistsException("Пользователь уже зарегистрирован в базе");
        }
        return userMapper.convertUserToUserDto(user);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User user = getUserByIdOrTrows(id);
        user.setName(Objects.nonNull(userDto.getName()) ? userDto.getName() : user.getName());
        user.setEmail(Objects.nonNull(userDto.getEmail()) ? userDto.getEmail() : user.getEmail());
        try {
        return userMapper.convertUserToUserDto(userRepository.save(user));

        } catch (DataIntegrityViolationException e) {

        throw new ObjectAlreadyExistsException("Пользователь c электронной почтой " + userDto.getEmail() + " зарегистрирован в базе");
        }
    }

    @Override
    public UserDto getUserById(long id) {
        return userMapper.convertUserToUserDto(getUserByIdOrTrows(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::convertUserToUserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(long id) {
        userRepository.delete(getUserByIdOrTrows(id));
    }

    private User getUserByIdOrTrows(long id) {
    return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }
}
