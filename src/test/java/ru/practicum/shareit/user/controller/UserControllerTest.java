package ru.practicum.shareit.user.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.service.UserServiceImpl;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.user.mapper.UserMapper;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import java.util.List;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserServiceImpl userService;

    private UserDto user1Dto;

    @BeforeEach
    void beforeEach() {
        user1Dto = UserMapper.toUserDto(new User(1L, "User1 name", "user1@mail.com"));
    }

    @Test
    void createUserTest() throws Exception {
        when(userService.create(any(UserDto.class)))
                .thenReturn(user1Dto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1Dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1Dto)));
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.update(anyLong(), any(UserDto.class)))
                .thenReturn(user1Dto);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(user1Dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1Dto)));
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(user1Dto));

        mockMvc.perform(get("/users/"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(user1Dto))));
    }

    @Test
    void getById() throws Exception {
        when(userService.getById(anyLong()))
                .thenReturn(user1Dto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1Dto)));
    }

    @Test
    void deleteUserTest() throws Exception {
        when(userService.delete(anyLong()))
                .thenReturn(user1Dto);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1Dto)));
    }
}