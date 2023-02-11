package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.ShareItApp.HEADER;
import static org.junit.jupiter.api.Assertions.*;


@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private UserDto user1Dto;

    private UserDto user2Dto;

    private ItemDto item1Dto;

    private ItemDtoBooking item1DtoBooking;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        User user1 = new User(1L, "User1 name", "user1@mail.com");
        user1Dto = UserMapper.toUserDto(user1);

        User user2 = new User(2L, "User2 name", "user2@mail.com");
        user2Dto = UserMapper.toUserDto(user2);

        Item item1 = Item.builder()
                .id(1L)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .itemRequest(null)
                .build();
        item1Dto = ItemMapper.toItemDto(item1);
        item1DtoBooking = ItemMapper.toItemDtoBooking(item1);

        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Comment1 text")
                .item(item1)
                .author(user2)
                .created(now)
                .build();
        comment1Dto = CommentMapper.toCommentDto(comment1);
    }


    private CommentDto comment1Dto;

    @Test
    void findAll() throws Exception {
        when(itemService.findAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of(item1DtoBooking));

        mockMvc.perform(get("/items")
                .header(HEADER, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(item1DtoBooking))));
    }

    @Test
    void findItem() throws Exception {
        when(itemService.findItem(anyLong(), anyLong()))
                .thenReturn(item1DtoBooking);

        mockMvc.perform(get("/items/1")
                .header(HEADER, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(item1DtoBooking)));
    }

    @Test
    void create() throws Exception {
        when(itemService.create(anyLong(), any(ItemDto.class)))
                .thenReturn(item1Dto);

        mockMvc.perform(post("/items")
                .content(mapper.writeValueAsString(item1Dto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HEADER, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(item1Dto)));
    }

    @Test
    void update() throws Exception {
        when((itemService.update(anyLong(), anyLong(), any(ItemDto.class))))
                .thenReturn(item1Dto);

        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(item1Dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(item1Dto)));
    }

    @Test
    void searchItem() throws Exception {
        when(itemService.searchItem(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(item1Dto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Item1")
                .header(HEADER, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(item1Dto))));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(comment1Dto);

        mockMvc.perform(post("/items/1/comment")
                .content(mapper.writeValueAsString(comment1Dto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HEADER, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(comment1Dto)));
    }
}