package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        return userRepository.createUser(userDto);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        return userRepository.updateUser(id, userDto);
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



}
