package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.mapper.UserMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final UserStorage storage;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        log.info("Пользователь с id{} создан", userDto.getId());
        User user = storage.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        User user = storage.findById(id).orElseThrow(() -> {
            log.warn("Пользователь c id{} не найден", id);
            throw new ObjectNotFoundException("Пользователь не найден");
        });
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(user.getEmail());
        }
        log.info("Пользователь с id{} обновлён", id);
        return UserMapper.toUserDto(storage.save(user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Пользователи отправлены");
        return storage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long id) {
        User user = storage.findById(id).orElseThrow(() -> {
            log.warn("Пользователь c id {} не найден", id);
            throw new ObjectNotFoundException("Пользователь не найден");
        });
        log.info("Пользователь с id{} отправлен", id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(long id) {
        log.info("Пользователь с id {} удалён", id);
        storage.findById(id).ifPresent(storage::delete);
    }
}
