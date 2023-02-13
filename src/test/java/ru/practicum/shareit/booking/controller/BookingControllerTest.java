package ru.practicum.shareit.booking.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingStatus;
import static ru.practicum.shareit.ShareItApp.HEADER;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.ArgumentMatchers.anyLong;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.booking.model.Booking;
import static org.mockito.ArgumentMatchers.any;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private UserDto user2Dto;
    private BookingDto booking1Dto;
    private BookingDtoResponse booking1DtoResponse;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        User user1 = new User(1L, "User1 name", "user1@mail.com");
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

        Booking booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        booking1Dto = BookingMapper.toBookingDto(booking1);
        booking1DtoResponse = BookingMapper.toBookingDtoResponse(booking1);
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(anyLong(), any(BookingDto.class)))
                .thenReturn(booking1DtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .content(mapper.writeValueAsString(booking1Dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, user2Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(booking1DtoResponse)));

    }

    @Test
    void changeStatus() throws Exception {
        when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking1DtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1")
                        .param("approved", "true")
                        .header(HEADER, user2Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(booking1DtoResponse)));


    }

    @Test
    void getById() throws Exception {
        when(bookingService.getBookingInfo(anyLong(), anyLong()))
                .thenReturn(booking1DtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header(HEADER, user2Dto.getId()))

                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(booking1DtoResponse)));
    }

    @Test
    void getByBooker() throws Exception {
        when(bookingService.getByBooker(anyLong(), any(String.class), anyInt(), anyInt()))
                .thenReturn(List.of(booking1DtoResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                .header(HEADER, user2Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(booking1DtoResponse))));
    }

    @Test
    void getByOwner() throws Exception {
        when(bookingService.getByOwner(anyLong(), any(String.class), anyInt(), anyInt()))
                .thenReturn(List.of(booking1DtoResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header(HEADER, user2Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(booking1DtoResponse))));
    }
}