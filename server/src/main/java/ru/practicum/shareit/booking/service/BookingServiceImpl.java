package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.UnsupportedStateException;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDtoResponse create(long bookerId, BookingDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> {
            throw new ObjectNotFoundException("Вещь не найдена");
        });
        User user = userRepository.findById(bookerId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Не тот пользователь");
        });
        if (item.getOwner().getId() == bookerId) throw new ObjectNotFoundException("Вы не можете забронировать вашу вещь");
        if (!item.getAvailable()) throw new BadRequestException("Вещь сейчас не доступна, для бронирования");
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BadRequestException("Не правильное время для бронирования");
        }
        bookingDto.setStatus(BookingStatus.WAITING);
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, item, user));
        BookingDtoResponse bookingDtoResponse = BookingMapper.toBookingDtoResponse(booking);
        log.info("Вещь создана");
        return bookingDtoResponse;
    }

    @Override
    @Transactional
    public BookingDtoResponse changeStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Бронирование не найдено");
        });
        Item item = booking.getItem();
        if (userId != item.getOwner().getId()) throw new ObjectNotFoundException("Вы не можете подтвердить это бронирование");
        if (booking.getStatus() == BookingStatus.APPROVED)
            throw new BadRequestException("Вы не можете сменить статус, после подтверждения");
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else booking.setStatus(BookingStatus.REJECTED);
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoResponse getBookingInfo(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Бронирование не найдено");
        });
        Item item = booking.getItem();
        if (booking.getBooker().getId() == userId || item.getOwner().getId() == userId) {
            return BookingMapper.toBookingDtoResponse(booking);
        } else throw new ObjectNotFoundException("В доступе отказано!");
    }

    @Override
    public List<BookingDtoResponse> getByBooker(long userId, String state, Pageable page) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь не найден");
        });

        List<Booking> books = new ArrayList<>();
        switch (state) {
            case "ALL":
                books.addAll(bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page));
                break;
            case "CURRENT":
                books.addAll(bookingRepository.findByBookerCurrent(userId, LocalDateTime.now(), page));
                break;
            case "PAST":
                books.addAll(bookingRepository.findByBookerPast(userId, LocalDateTime.now(), page));
                break;
            case "FUTURE":
                books.addAll(bookingRepository.findByBookerFuture(userId, LocalDateTime.now(), page));
                break;
            case "WAITING":
                books.addAll(bookingRepository.findByBookerAndStatus(userId, BookingStatus.WAITING, page));
                break;
            case "REJECTED":
                books.addAll(bookingRepository.findByBookerAndStatus(userId, BookingStatus.REJECTED, page));
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
        return books.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResponse> getByOwner(long userId, String state, Pageable page) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь не найден");
        });

        List<Booking> books = new ArrayList<>();
        switch (state) {
            case "ALL":
                books.addAll(bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, page));
                break;
            case "CURRENT":
                books.addAll(bookingRepository.findByItemOwnerCurrent(userId, LocalDateTime.now(), page));
                break;
            case "PAST":
                books.addAll(bookingRepository.findByItemOwnerPast(userId, LocalDateTime.now(), page));
                break;
            case "FUTURE":
                books.addAll(bookingRepository.findByItemOwnerFuture(userId, LocalDateTime.now(), page));
                break;
            case "WAITING":
                books.addAll(bookingRepository.findByItemOwnerAndStatus(userId, BookingStatus.WAITING, page));
                break;
            case "REJECTED":
                books.addAll(bookingRepository.findByItemOwnerAndStatus(userId, BookingStatus.REJECTED, page));
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
        return books.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }
}
