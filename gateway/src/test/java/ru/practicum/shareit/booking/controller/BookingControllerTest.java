package ru.practicum.shareit.booking.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingState;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import lombok.SneakyThrows;
import java.util.List;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingClient bookingClient;


    BookingDto getCreateBookingDto() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.now().plusDays(2L));
        dto.setEnd(LocalDateTime.now().plusDays(3L));
        return dto;
    }

    BookingDto getBookingDto() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStatus(BookingStatus.WAITING);
        return dto;
    }

    @SneakyThrows
    @Test
    void create_whenCorrectCreateBookingDto_thenReturnedSavedBookingDto() {
        BookingDto createBookingDto = getCreateBookingDto();
        BookingDto savedBookingDto = getBookingDto();
        savedBookingDto.setStart(createBookingDto.getStart());
        savedBookingDto.setEnd(createBookingDto.getEnd());
        long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(201).body(savedBookingDto);
        when(bookingClient.create(userId, createBookingDto)).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(createBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(savedBookingDto)));
    }

    @SneakyThrows
    @Test
    void create_whenStartAfterEnd_thenStatusIsBadRequest() {
        BookingDto createBookingDto = getCreateBookingDto();
        createBookingDto.setStart(LocalDateTime.now().plusDays(5));
        createBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        Long userId = 1L;

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(createBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void setApprove_whenApproved_thenReturnedUpdatedBookingDto() {
        long bookingId = 1L;
        long ownerId = 1L;
        BookingDto dto = getBookingDto();
        dto.setStatus(BookingStatus.APPROVED);
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(bookingClient.changeStatus(bookingId, ownerId, true)).thenReturn(response);

        mockMvc.perform(patch("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getAllForUserByState_whenStateIsCorrect_thenReturnedListOfBookingDtos() {
        BookingState state = BookingState.ALL;
        Integer from = 0;
        Integer size = 10;
        long userId = 1L;
        List<BookingDto> dtoList = List.of(getBookingDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(bookingClient.getByBooker(userId, String.valueOf(state), from, size))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", String.valueOf(state))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void getAllForUserByState_whenStateIsIncorrect_thenStatusIsBadRequest() {
        String state = "UNKNOWN";
        Integer from = 0;
        Integer size = 10;
        Long userId = 1L;
        String error = "Unknown state: UNKNOWN";

        mockMvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.error", is(error)));
    }

    @SneakyThrows
    @Test
    void getAllForOwnerByState_whenStateIsCorrect_thenReturnedListOfBookingDtos() {
        BookingState state = BookingState.ALL;
        Integer from = 0;
        Integer size = 10;
        long ownerId = 1L;
        List<BookingDto> dtoList = List.of(getBookingDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(bookingClient.getByOwner(ownerId, String.valueOf(state), from, size))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", String.valueOf(state))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void getById_whenBookingFound_thenReturnedBookingDto() {
        long bookingId = 1L;
        long userId = 1L;
        BookingDto dto = getBookingDto();
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(bookingClient.getBookingInfo(bookingId, userId)).thenReturn(response);

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }
}
