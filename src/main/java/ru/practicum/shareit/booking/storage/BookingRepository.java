package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    @Query("select booking from Booking booking " +
            "where booking.start < ?2 " +
            "and booking.end > ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.start")
    List<Booking> findByBookerCurrent(long userId, LocalDateTime now);

    @Query("select booking from Booking booking " +
            "where booking.end < ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByBookerPast(long userId, LocalDateTime end);

    @Query("select booking from Booking booking " +
            "where booking.start > ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByBookerFuture(long userId, LocalDateTime start);

    @Query("select booking from Booking booking " +
            "where booking.status = ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.status desc")
    List<Booking> findByBookerAndStatus(long userId, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId);

    @Query("select booking from Booking booking " +
            "where booking.start < ?2 " +
            "and booking.end > ?2 " +
            "and booking.item.owner.id = ?1 " +
            "order by booking.start")
    List<Booking> findByItemOwnerCurrent(long userId, LocalDateTime now);

    @Query("select booking from Booking booking " +
            "where booking.end < ?2 " +
            "and booking.item.owner.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByItemOwnerPast(long userId, LocalDateTime end);

    @Query("select booking from Booking booking " +
            "where booking.start > ?2 " +
            "and booking.item.owner.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByItemOwnerFuture(long userId, LocalDateTime start);

    @Query("select booking from Booking booking " +
            "where booking.status = ?2 " +
            "and booking.item.owner.id = ?1 " +
            "order by booking.status desc")
    List<Booking> findByItemOwnerAndStatus(long userId, BookingStatus status);

    @Query("select distinct booking from Booking booking " +
            "where booking.end < ?2 " +
            "and booking.item.id = ?1 " +
            "order by booking.start desc ")
    Optional<Booking> findLastBooking(long itemId, LocalDateTime now);

    @Query("select distinct booking from Booking booking " +
            "where booking.start > ?2 " +
            "and booking.item.id = ?1 " +
            "order by booking.start ")
    Optional<Booking> findNextBooking(long itemId, LocalDateTime now);

    Optional<Booking> findByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime end);
}
