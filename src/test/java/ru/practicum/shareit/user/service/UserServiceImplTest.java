package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exeption.ObjectNotFoundException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import java.util.Optional;
import org.mockito.Mock;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    private User user1;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "User1 name", "user1@mail.com");
    }

    @Test
    void createUserTest() {
        when(repository.save(any(User.class))).thenReturn(user1);

        UserDto userDto = service.create(
                UserMapper.toUserDto(user1));

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }


    @Test
    void updateUserWithEmailFormatTest() {
    when(repository.findById(anyLong()))
            .thenReturn(Optional.ofNullable(user1));
    when(repository.save(any(User.class)))
            .thenReturn(user1);

    UserDto userDto = UserMapper.toUserDto(user1);
    service.update(userDto.getId(), userDto);

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void updateUserWithNoUser() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        UserDto userDto = UserMapper.toUserDto(user1);
        userDto.setId(10L);
        ObjectNotFoundException exc = assertThrows(ObjectNotFoundException.class,
                () ->service.update(1L, userDto)
        );

        assertEquals("Пользователь не найден", exc.getMessage());
    }

    @Test
    void getAllUsersWhenUserFoundThenReturnedUser() {

        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(repository.save(any(User.class)))
                .thenReturn(user1);

        UserDto userDto = UserMapper.toUserDto(user1);
        service.update(userDto.getId() ,userDto);

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void getAllUsersWhenUserFoundThenUserNotFoundExceptionThrown() {
        long userId = 0L;
        User expectedUser = new User();
        when(repository.findById(userId))
                .thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> service.getById(userId));
    }

    @Test
    void getByIdTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        UserDto userDto = service.getById(user1.getId());

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void getUserWrongIdTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> service.getById(user1.getId()));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void deleteUserTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        UserDto userDto = service.delete(user1.getId());

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void deleteUserTestWithNoUser() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        UserDto userDto = UserMapper.toUserDto(user1);
        userDto.setId(10L);
        ObjectNotFoundException exc = assertThrows(ObjectNotFoundException.class,
                () -> service.delete(1L)
        );

        assertEquals("Пользователь не найден", exc.getMessage());
    }

    @Test
    void getAllUsersTest() {
        when(repository.findAll())
                .thenReturn(List.of(user1));

        List<UserDto> userDtos = service.getAllUsers();

        assertEquals(1, userDtos.size());
        assertEquals(1, userDtos.get(0).getId());
        assertEquals("User1 name", userDtos.get(0).getName());
        assertEquals("user1@mail.com", userDtos.get(0).getEmail());
    }
}