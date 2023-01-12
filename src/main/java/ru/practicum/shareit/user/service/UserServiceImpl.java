package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storige.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public User create(User user) {
        emailChecker(user.getUserEmail());
        log.info("Пользователь создан");
        return userStorage.create(user);
    }

    public User update(int id, UserDto userDto) {
        if (userDto.getUserEmail() != null) {
            emailChecker(userDto.getUserEmail());
        }
        log.info("Пользователь обновлён");
        return userStorage.update(id, userDto);
    }

    public User getById(int id) {
        if (!userStorage.findAll().contains(id)) {
            log.warn("Пользователь с id{} не найден", id);
            throw new ObjectNotFoundException("Пользователь найден");
        }
        return userStorage.getById(id);
    }

    public User deleteById(int id) {
        log.info("Пользователь с id{} удалён", id);
        return deleteById(id);
    }

    public List<User> findAll() {
        log.info("Список пользователей отправлен");
        return userStorage.findAll();
    }

    private void emailChecker(String email) {
        List <User> temp = findAll();
        boolean flag = true;
        for (User usr : temp) {
            if (usr.getUserEmail().equals(email)) {
                flag = false;
            }
        }
        if (!flag) {
            log.warn("Пользователь уже существует");
            throw new ValidationException("Пользователь уже существует");
        }
    }

}
