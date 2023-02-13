package ru.practicum.shareit.booking.storage;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.BookingStatus;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import static org.junit.jupiter.api.Assertions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;

    User user = new User(
            null,
            "name",
            "email@email.ru");
    User user2 = new User(
            null,
            "name2",
            "email2@email.ru");
    Item item = new Item(
            null,
            "name",
            "description",
            true,
            user,
            null);
    Booking booking = new Booking(
            null,
            LocalDateTime.now().minusHours(3),
            LocalDateTime.now().minusHours(1),
            item,
            user2,
            null);

    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findAllByBookerIdOrderByStartDescTest() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(user2.getId(), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));//id могут сбиться, проверить при запуске всех тестов
    }

    @Test
    void findByBookerCurrentTest() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findByBookerCurrent(user2.getId(), LocalDateTime.now().minusHours(2), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByBookerPastTest() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findByBookerPast(user2.getId(), LocalDateTime.now().plusHours(2), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByBookerFutureTest() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findByBookerFuture(user2.getId(), LocalDateTime.now().minusHours(4), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByBookerAndStatusTest() {
        booking.setStatus(BookingStatus.WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findByBookerAndStatus(user2.getId(), BookingStatus.WAITING, pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findAllByOwnerIdOrderByStartDescTest() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findByItemOwnerIdOrderByStartDesc(user.getId(), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));//id могут сбиться, проверить при запуске всех тестов
    }

    @Test
    void findByItemOwnerCurrentTest() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findByItemOwnerCurrent(user.getId(), LocalDateTime.now().minusHours(2), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByItemOwnerPastTest() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findByItemOwnerPast(user.getId(), LocalDateTime.now().plusHours(2), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByItemOwnerFutureTest() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findByItemOwnerFuture(user.getId(), LocalDateTime.now().minusHours(4), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByItemOwnerAndStatusTest() {
        booking.setStatus(BookingStatus.WAITING);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findByItemOwnerAndStatus(user.getId(), BookingStatus.WAITING, pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByBookerIdAndItemIdAndEndBeforeTest() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);

        Booking res = bookingRepository.findByBookerIdAndItemIdAndEndBefore(user2.getId(), item.getId(),
                LocalDateTime.now().plusHours(1)).orElseThrow();
        assertEquals(booking, res);
    }

    @Test
    void findBookingsLastTest() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        Booking booking1 = booking;
        booking1.setEnd(LocalDateTime.now().minusHours(6));
        booking1.setStart(LocalDateTime.now().minusHours(5));
        em.persist(booking1);

        List<Booking> res = bookingRepository.findBookingsLast(List.of(item.getId()), LocalDateTime.now(), user.getId());

        assertEquals(1, res.size());
        assertEquals(booking, res.get(0));
    }

    @Test
    void findBookingsNextTest() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        booking.setEnd(LocalDateTime.now().plusHours(7));
        booking.setStart(LocalDateTime.now().plusHours(6));
        em.persist(booking);
        Booking booking1 = booking;
        booking1.setEnd(LocalDateTime.now().plusHours(4));
        booking1.setStart(LocalDateTime.now().plusHours(3));
        em.persist(booking1);

        List<Booking> res = bookingRepository.findBookingsNext(List.of(item.getId()), LocalDateTime.now(), user.getId());

        assertEquals(1, res.size());
        assertEquals(booking1, res.get(0));
    }
}