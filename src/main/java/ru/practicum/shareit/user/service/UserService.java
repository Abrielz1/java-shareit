package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;


public interface UserService {



    public User create(User user);

    public User update(int id, UserDto userDto);

    public User getById(int id);

    public User deleteById(int id);

    List<User> findAll();
}
