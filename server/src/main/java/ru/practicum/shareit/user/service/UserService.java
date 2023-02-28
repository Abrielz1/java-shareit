package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(long id, UserDto userDto);

    List<UserDto> getAllUsers();

    UserDto getById(long id);

    UserDto delete(long id);
}