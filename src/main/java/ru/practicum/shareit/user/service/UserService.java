package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getById(long id);

    UserDto create(UserDto userDto);

    UserDto update(long id, UserDto userDto);

    UserDto delete(long id);
}