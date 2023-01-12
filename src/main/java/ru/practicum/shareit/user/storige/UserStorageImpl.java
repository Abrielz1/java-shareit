package ru.practicum.shareit.user.storige;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserStorageImpl implements UserStorage {

    private long idCounter = 1;

    private final Map<Long, User> storage = new HashMap<>();

    @Override
    public User create(User user) {
        user.setUserId(idCounter++);
        storage.put(user.getUserId(), user);
        return user;
    }

    @Override
    public User update(int id, UserDto userDto) {
        if (storage.containsKey(id)) {
            if (userDto.getUserName() != null) {
                storage.get(id).setUserName(userDto.getUserName());
            }
            if (userDto.getUserEmail() != null) {
                storage.get(id).setUserEmail(userDto.getUserEmail());
            }
            return storage.get(id);
        } else  {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public User getById(int id) {
        return storage.get(id);
    }

    @Override
    public User deleteById(int id) {
        return storage.remove(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }
}
