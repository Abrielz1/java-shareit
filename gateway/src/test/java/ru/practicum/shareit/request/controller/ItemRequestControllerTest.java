package ru.practicum.shareit.request.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.request.client.ItemRequestClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import lombok.SneakyThrows;
import java.util.List;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestClient itemRequestClient;

    ItemRequestDto getRequestDto() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("TestDescription");
        dto.setCreated(LocalDateTime.now());
        return dto;
    }

    @SneakyThrows
    @Test
    void create_whenInvoked_thenReturnedSavedRequestDto() {
        long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("TestDescription");
        ItemRequestDto savedDto = getRequestDto();
        ResponseEntity<Object> response = ResponseEntity.status(200).body(savedDto);
        when(itemRequestClient.create(userId, requestDto)).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(savedDto)));
    }

    @SneakyThrows
    @Test
    void getById_whenRequestFound_thenReturnedRequestDto() {
        long userId = 1L;
        long requestId = 1L;
        ItemRequestDto dto = getRequestDto();
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(itemRequestClient.getRequestInfo(requestId, userId)).thenReturn(response);

        mockMvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getOwn_whenRequestsFound_thenReturnedListOfRequestDtos() {
        long userId = 1L;
        List<ItemRequestDto> dtoList = List.of(getRequestDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(itemRequestClient.getRequestsInfo(userId)).thenReturn(response);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void getAll_whenRequestsFound_thenReturnedListOfRequestDtos() {
        long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<ItemRequestDto> dtoList = List.of(getRequestDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(itemRequestClient.getRequestsList(userId, from, size)).thenReturn(response);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }


}
