package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDto;
import java.util.List;

public interface BookingService {
    BookingDtoResponse create(long id, BookingDto bookingDto);

    BookingDtoResponse changeStatus(long userId, long bookingId, boolean approved);

    BookingDtoResponse getBookingInfo(long userId, long bookingId);

    List<BookingDtoResponse> getByBooker(long userId, String state, int from, int size);

    List<BookingDtoResponse> getByOwner(long userId, String state, int from, int size);
}