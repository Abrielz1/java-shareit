package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;


    private LocalDateTime now;

    private LocalDateTime start;

    private LocalDateTime end;

    private User user1;

    private User user2;

    private Item item1;

    private Booking booking1;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
        user1 = new User(1L, "User1 name", "user1@mail.com");
        user2 = new User(2L, "User2 name", "user2@mail.com");
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        item1 = Item.builder()
                .id(1L)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .itemRequest(null)
                .build();
        item1 = itemRepository.save(item1);

        booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        booking1 = bookingRepository.save(booking1);
    }

    @AfterEach
    void afterEach() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }


    @Test
    void findAllByBookerIdOrderByStartDescTest() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(
                user2.getId(), PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }

    @Test
    void indByBookerIdOrderByStartDescTest() {
        booking1.setStart(start.minusDays(5));
        booking1.setEnd(end.plusDays(5));
        bookingRepository.save(booking1);
        List<Booking> bookings = bookingRepository.findByBookerCurrent(
                user2.getId(), now, PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }

    @Test
    void findByBookerIdAndEndBeforeOrderByStartDescTest() {
        booking1.setStart(start.minusDays(5));
        booking1.setEnd(end.minusDays(3));
        bookingRepository.save(booking1);

        List<Booking> bookings = bookingRepository.findByBookerPast(
                user2.getId(), now, PageRequest.of(0, 10));
    }

    @Test
    void findByBookerIdAndStartAfterOrderByStartDescTest() {
        List<Booking> bookings = bookingRepository.findByBookerFuture(
                user2.getId(), now, PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDescTest() {
        List<Booking> bookings = bookingRepository.findByBookerAndStatus(
                user2.getId(), BookingStatus.WAITING, PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }
    @Test
    void findByItemOwnerIdOrderByStartDescTest() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(
                user1.getId(), PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }

    @Test
    void findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDescTest() {
        booking1.setStart(start.minusDays(5));
        booking1.setEnd(end.plusDays(5));
        bookingRepository.save(booking1);
        List<Booking> bookings = bookingRepository.findByBookerCurrent(
                user1.getId(), now, PageRequest.of(0, 10));

    //    assertEquals(List.of(booking1).size(), bookings.size());
    }

    @Test
    void findByItemOwnerIdAndEndBeforeOrderByStartDescTest() {
        booking1.setStart(start.minusDays(5));
        booking1.setEnd(end.minusDays(3));
        bookingRepository.save(booking1);
        List<Booking> bookings = bookingRepository.findByBookerPast(
                user1.getId(), now, PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);

    }

    @Test
    void findByItemOwnerIdAndStartAfterOrderByStartDescTest() {
        List<Booking> bookings = bookingRepository.findByBookerFuture(
                user1.getId(), now, PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStartDescTest() {
        List<Booking> bookings = bookingRepository.findByBookerAndStatus(
                user1.getId(), BookingStatus.WAITING, PageRequest.of(0, 10));

        assertEquals(List.of(booking1), bookings);
    }



    @Test
    void findBookingsLast() {

        List<Long> ids = userRepository.findAll().stream()
                .map(User::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findBookingsLast(
                ids, now,  user1.getId());
        assertEquals(List.of(booking1).size(), bookings.size());
    }

    @Test
    void findBookingsNext() {
        List<Long> ids = userRepository.findAll().stream()
                .map(User::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findBookingsNext(
                ids, now,  user1.getId());
        assertEquals(List.of(booking1).size(), bookings.size());
    }
}