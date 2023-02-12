package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.exeption.UnsupportedStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private LocalDateTime start;

    private LocalDateTime end;

    private User user1;

    private User user2;

    private User user3;

    private Item item1;

    private Booking booking1;

    @BeforeEach
    void beforeEach() {

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);

        user1 = new User(1L, "User1 name", "user1@mail.com");
        user2 = new User(2L, "User2 name", "user2@mail.com");
        user3 = new User(3L, "User3 name", "user3@mail.com");

        item1 = Item.builder()
                .id(1L)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .itemRequest(null)
                .build();

        booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingDtoResponse bookingDtoResponse = bookingService.create(
                user2.getId(),
                BookingMapper.toBookingDto(booking1)
                );

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
    }

    @Test
    void createBookingWithBookerAsOwnerUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toBookingDto(booking1)
                        ));

        assertEquals("Вы не можете забронировать вашу вещь", exception.getMessage());
    }

    @Test
    void createBookingOnNotAvailableItemTest() {
        item1.setAvailable(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toBookingDto(booking1)));

        assertEquals("Вы не можете забронировать вашу вещь", exception.getMessage());
    }

    @Test
    void createBookingWithWrongTimeTest() {
        booking1.setStart(LocalDateTime.now().minusDays(3));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toBookingDto(booking1)));

        assertEquals("Вы не можете забронировать вашу вещь", exception.getMessage());
    }

    @Test
    void createBookingOnNotExistingItemTest() {
        booking1.setStart(LocalDateTime.now().minusDays(3));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toBookingDto(booking1)));

        assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    void createBookingWithWrongUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(1L,
                        BookingMapper.toBookingDto(booking1)));

        assertEquals("Не тот пользователь", exception.getMessage());
    }

    //todo createBookingWithEndTimeBeforeStartTime

    //   @Test
    //   void createBookingWithEndTimeBeforeStartTime() {
//
//          booking1.setEnd(LocalDateTime.now().plusDays(3));
//          when(userRepository.findById(anyLong()));
//
//        when(userRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(user1));
//
//        when(itemRepository.findById(anyLong()))
//                .thenReturn(Optional.ofNullable(item1));
//
//        when(bookingRepository.save(any(Booking.class)))
//                .thenReturn(booking1);
//
//        BookingDtoResponse bookingDtoResponse = bookingService.create(
//                user2.getId(),
//                BookingMapper.toBookingDto(booking1)
//        );

//        booking1.setStart(LocalDateTime.now().plusDays(6));
//        when(userRepository.findById(anyLong()));
//
//        booking1.setEnd(LocalDateTime.now().minusDays(1));
//        when(userRepository.findById(anyLong()));


//        BadRequestException exception = assertThrows(BadRequestException.class,
//                () -> bookingService.create(user2.getId(),
//                        BookingMapper.toBookingDto(booking1)));
//
//        assertEquals("Не правильное время для бронирования", exception.getMessage());
//    }

    @Test
    void changeStatus() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingDtoResponse bookingDtoResponse = bookingService.changeStatus(
                user1.getId(),
                booking1.getId(),
                true);

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
        assertEquals(BookingStatus.APPROVED, bookingDtoResponse.getStatus());
    }

    @Test
    void updateBookingWithWrongIdTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.changeStatus(
                        user2.getId(),
                        booking1.getId(),
                        true));

        assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void updateBookingFromWrongUserTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.changeStatus(
                        user2.getId(),
                        booking1.getId(),
                        true));

        assertEquals("Вы не можете подтвердить это бронирование", exception.getMessage());
    }

    @Test
    void changeStatusBookingStatusApprovedTwiceTest() {

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        booking1.setStatus(BookingStatus.APPROVED);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.changeStatus(
                        user1.getId(),
                        booking1.getId(),
                        true));

        assertEquals("Вы не можете сменить статус, после подтверждения", exception.getMessage());
    }

    @Test
    void updateBookingRejectTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingDtoResponse bookingDtoResponse = bookingService.changeStatus(
                user1.getId(),
                booking1.getId(),
                false);

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
        assertEquals(BookingStatus.REJECTED, bookingDtoResponse.getStatus());
    }

    @Test
    void getBookingTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        BookingDtoResponse bookingDtoResponse = bookingService.getBookingInfo(
                booking1.getId(),
                user1.getId());

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponse.getStatus());
    }

    @Test
    void getBookingInfoBookingNotFound() { //не покрывает почему-то
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingInfo(
                        user1.getId(),
                        booking1.getId()));

        assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void getBookingInfoYouNotABooker() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        booking1.setBooker(user2);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingInfo(
                        5L,
                        booking1.getId()));

        assertEquals("В доступе отказано!", exception.getMessage());
    }

    @Test
    void getBookingsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByBooker(user1.getId(),
                "ALL",
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsCurrentStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerCurrent(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByBooker(user1.getId(),
                "CURRENT",
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsPastStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerPast(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByBooker(user1.getId(),
                "PAST",
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsFutureStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerFuture(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByBooker(user1.getId(),
                "FUTURE",
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsWaitingStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerAndStatus(
                anyLong(),
                any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByBooker(user1.getId(),
                "WAITING",
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsRejectedStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerAndStatus(
                anyLong(),
                any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByBooker(user1.getId(),
                "REJECTED",
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsUnknownStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        UnsupportedStateException exception = assertThrows(UnsupportedStateException.class,
                () -> bookingService.getByBooker(user1.getId(),
                        "UNKNOWN",
                        0,
                        10));

        assertEquals("Статус не известен: UNKNOWN", exception.getMessage());
    }

    @Test
    void getBookingsWithWrongUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        booking1.setBooker(user2);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getByBooker(
                        5L,
                        "WAITING",
                        0,
                        10));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void getItemsOwnerBookingsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByOwner(user1.getId(),
                "ALL",
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsCurrentStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerCurrent(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByOwner(user1.getId(),
                "CURRENT",
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsPastStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerPast(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByOwner(user1.getId(),
                "PAST",
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsFutureStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerFuture(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByOwner(user1.getId(),
                "FUTURE",
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsWaitingStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerAndStatus(
                anyLong(),
                any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByOwner(user1.getId(),
                "WAITING",
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsRejectedStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerAndStatus(
                anyLong(),
                any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByOwner(user1.getId(),
                "REJECTED",
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsUnknownStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        UnsupportedStateException exception = assertThrows(UnsupportedStateException.class,
                () -> bookingService.getByOwner(user1.getId(),
                        "UNKNOWN",
                        0,
                        10));

        assertEquals("Статус не известен: UNKNOWN", exception.getMessage());
    }

    @Test
    void getItemsOwnerWithWrongUser() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getByOwner(user1.getId(),
                        "WAITING",
                        0,
                        10));

        assertEquals("Пользователь не найден", exception.getMessage());
    }
}