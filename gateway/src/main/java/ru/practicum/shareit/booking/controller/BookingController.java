package ru.practicum.shareit.booking.controller;

import ru.practicum.shareit.exeption.UnsupportedStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import static ru.practicum.shareit.common.Variables.HEADER;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingState;
import javax.validation.constraints.PositiveOrZero;
import ru.practicum.shareit.booking.dto.BookingDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import javax.validation.constraints.Positive;
import ru.practicum.shareit.common.Create;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER) long id, @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BadRequestException("Не правильное время для бронирования");
        }
        return bookingClient.create(id, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeStatus(@RequestHeader(HEADER) long userId,
                                               @PathVariable long bookingId,
                                               @RequestParam boolean approved) {
        return bookingClient.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(HEADER) long userId,
                                          @PathVariable long bookingId) {
        return bookingClient.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getByBooker(@RequestHeader(HEADER) long userId,
                                              @RequestParam(defaultValue = "ALL", required = false) String state,
                                              @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                              @Positive @RequestParam(defaultValue = "20", required = false) int size) {
        BookingState status = BookingState.from(state).orElseThrow(() -> new UnsupportedStateException("Unknown state: " + state));
        return bookingClient.getByBooker(userId, String.valueOf(status), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(@RequestHeader(HEADER) long userId,
                                             @RequestParam(defaultValue = "ALL", required = false) String state,
                                             @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                             @Positive @RequestParam(defaultValue = "20", required = false) int size) {
        return bookingClient.getByOwner(userId, state, from, size);
    }
}
