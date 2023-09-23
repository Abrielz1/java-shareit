package ru.practicum.shareit.item.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.client.ItemClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.ItemDto;
import org.springframework.http.MediaType;
import static org.mockito.Mockito.when;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import lombok.SneakyThrows;
import java.util.List;
import lombok.Getter;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemClient itemClient;


    ItemDto getCreateItemDto() {
        return ItemDto.builder()
                .name("TestName")
                .description("TestDescription")
                .available(Boolean.TRUE)
                .build();
    }

    ItemDto getItemDto() {
        return ItemDto.builder()
                .name("TestName")
                .description("TestDescription")
                .available(Boolean.TRUE)
                .build();
    }

    ItemDto getPatchItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("UpdatedName")
                .description("UpdatedDescription")
                .available(Boolean.TRUE)
                .build();
    }

    ItemDto getUpdatedItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("UpdatedName")
                .description("UpdatedDescription")
                .available(Boolean.TRUE)
                .build();
    }

    CommentDto getCommentDto() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test");
        return commentDto;
    }

    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String error;
    }

    @SneakyThrows
    @Test
    void create_whenInvoked_thenStatusIsCreatedAndReturnedItemDto() {
        ItemDto createItemDto = getCreateItemDto();
        long ownerId = 1L;
        ItemDto savedDto = getItemDto();
        ResponseEntity<Object> response = ResponseEntity.status(201).body(savedDto);
        when(itemClient.create(ownerId, createItemDto)).thenReturn(response);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(createItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(savedDto)));
    }

    @SneakyThrows
    @Test
    void patch_whenItemFound_thenReturnUpdatedItemDto() {
        ItemDto patchItemDto = getPatchItemDto();
        ItemDto updatedItemDto = getUpdatedItemDto();
        long itemId = 1L;
        long ownerId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(200).body(updatedItemDto);
        when(itemClient.update(itemId, ownerId, patchItemDto)).thenReturn(response);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .content(objectMapper.writeValueAsString(patchItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedItemDto)));
    }

    @SneakyThrows
    @Test
    void getById_whenItemFound_thenReturnedItemDto() {
        long itemId = 1L;
        long userId = 1L;
        ItemDto dto = getItemDto();
        dto.setId(1L);
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(itemClient.findItem(itemId, userId)).thenReturn(response);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void search_whenItemsFound_thenReturnedListOfItemDtos() {
        long userId = 1L;
        String text = "test";
        List<ItemDto> dtoList = List.of(getItemDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(itemClient.searchItem(text, userId, 0, 10)).thenReturn(response);

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void createComment_whenInvoked_thenReturnedSavedCommentDto() {
        long itemId = 1L;
        long userId = 1L;
        CommentDto dto = getCommentDto();
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(itemClient.addComment(userId, itemId, dto)).thenReturn(response);

        mockMvc.perform(post("/items/{id}/comment", itemId)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }
}