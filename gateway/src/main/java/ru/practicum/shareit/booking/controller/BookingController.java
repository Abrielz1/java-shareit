package ru.practicum.shareit.booking.controller;

import ru.practicum.shareit.exeption.UnsupportedStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.Create;

@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long id, @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BadRequestException("Не правильное время для бронирования");
        }
        return bookingClient.create(id, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable long bookingId,
                                               @RequestParam boolean approved) {
        return bookingClient.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long bookingId) {
        return bookingClient.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getByBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(defaultValue = "ALL", required = false) String state,
                                              @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                              @Positive @RequestParam(defaultValue = "20", required = false) int size) {
        BookingState status = BookingState.from(state).orElseThrow(() -> new UnsupportedStateException("Unknown state: " + state));
        PageRequest page = PageRequest.of(from / size, size);
        return bookingClient.getByBooker(userId, String.valueOf(status), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam(defaultValue = "ALL", required = false) String state,
                                             @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                             @Positive @RequestParam(defaultValue = "20", required = false) int size) {
        return bookingClient.getByOwner(userId, state, from, size);
    }
}
