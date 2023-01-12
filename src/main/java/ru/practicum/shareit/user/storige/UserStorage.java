package ru.practicum.shareit.user.storige;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {
    User create(User user);
    User update(int id, UserDto userDto);
    User getById(int id);
    User deleteById(int id);
    List<User> findAll();
}
