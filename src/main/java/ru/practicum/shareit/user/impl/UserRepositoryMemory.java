package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
@Slf4j
public class UserRepositoryMemory implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final List<String> emails = new ArrayList<>();

    private static long countId = 0;

    private final UserMapper mapper;

    private long assignId(User user) {
        user.setId(++countId);
        return countId;
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        User user = mapper.convertUserDtoToUser(userDto);

      if (!users.containsValue(user)) {
          users.put(assignId(user), user);
          emails.add(userDto.getEmail());
          return mapper.convertUserToUserDto(user);
      }
      log.info("Пользователь {} уже существует", user);
      throw new ObjectAlreadyExistsException("Пользователь " + user.getName() + " " + user.getEmail() + " уже существует");
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
       checkUserExist(id);
       User storedUser = users.get(id);
       storedUser.setName(Objects.nonNull(userDto.getName()) ? userDto.getName() : storedUser.getName());

       if (Objects.nonNull(userDto.getEmail())) {
       updateUserEmail(userDto, storedUser);
       }
       return mapper.convertUserToUserDto(storedUser);
    }

    private void updateUserEmail(UserDto user, User storedUser) {

        if (!user.getEmail().equals(storedUser.getEmail())) {
            if (emails.contains(user.getEmail())) {
                log.info("Адрес электронной {} почты уже используется", user.getEmail());
                throw new ObjectAlreadyExistsException("Ошибка при обновлении данных пользователя, " +
                        "указанный адрес электронной почты уже используется");
            }
            emails.remove(storedUser.getEmail());
            emails.add(user.getEmail());
            storedUser.setEmail(user.getEmail());
        }
    }

    @Override
    public UserDto getUserById(long id) {
        checkUserExist(id);
        return mapper.convertUserToUserDto(users.get(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(mapper::convertUserToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteUserById(long userId) {
        if (!users.containsKey(userId)) {
            return false;
        }
        users.remove(userId);
        return true;
    }

    private void checkUserExist(long id) throws NotFoundException {
        if (!users.containsKey(id)) {
            log.info("Пользователя с id = {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }
}
