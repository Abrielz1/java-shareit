package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.Create;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {

    private final String HEADER = "X-Sharer-User-Id";//код стиль тута падает
                                                     //Не получается пок красоте сделать

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse create(@RequestHeader("X-Sharer-User-Id") long id, @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        return bookingService.create(id, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse changeStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long bookingId,
                                           @RequestParam boolean approved) {
        return bookingService.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoResponse> getByBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getByOwner(userId, state);
    }
}
