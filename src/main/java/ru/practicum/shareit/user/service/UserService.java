package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(long id, UserDto userDto);

    List<UserDto> getAllUsers();

    UserDto getById(long id);

    void delete(long id);
}
