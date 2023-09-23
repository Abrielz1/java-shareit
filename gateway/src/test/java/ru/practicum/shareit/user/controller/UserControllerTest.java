package ru.practicum.shareit.user.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.user.client.UserClient;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import lombok.SneakyThrows;
import java.util.List;
import lombok.Getter;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    UserDto getTestUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("John Doe");
        dto.setEmail("jdoe@mail.com");
        return dto;
    }

    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String error;
    }

    @SneakyThrows
    @Test
    void create_whenInvoked_thenStatusIsCreatedAndReturnedUserDto() {
        UserDto dto = getTestUserDto();
        ResponseEntity<Object> response = ResponseEntity.status(201).body(dto);
        when(userClient.create(dto)).thenReturn(response);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void update_whenInvoked_thenStatusIsOkAndReturnedUserDto() {
        UserDto dto = getTestUserDto();
        long userId = 1L;

        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(userClient.update(userId, dto)).thenReturn(response);

        mockMvc.perform(patch("/users/{id}", userId)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void  getById_whenUserFound_thenReturnedUserDto() {
        UserDto dto = getTestUserDto();
        long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(userClient.getById(userId)).thenReturn(response);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getById_whenUserNotFound_thenStatusNotFound() {
        long userId = 0L;
        String errorMessage = "User with id " + userId + " not found";
        ResponseEntity<Object> response = ResponseEntity.status(404).body(new ErrorResponse(errorMessage));
        when(userClient.getById(userId)).thenReturn(response);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(errorMessage)));
    }

    @SneakyThrows
    @Test
    void getAll_whenUsersFound_thenReturnedListOfUsersDto() {
        List<UserDto> dtoList = List.of(getTestUserDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(userClient.getAllUsers()).thenReturn(response);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void deleteById_whenUserFound_thenStatusIsOk() {
        long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(200).build();


        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

    }
}