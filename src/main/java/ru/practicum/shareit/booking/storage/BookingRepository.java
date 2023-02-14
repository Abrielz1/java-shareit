package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId, Pageable pageable);

    @Query("select booking from Booking booking " +
            "where booking.start < ?2 " +
            "and booking.end > ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.start")
    List<Booking> findByBookerCurrent(long userId, LocalDateTime now, Pageable pageable);

    @Query("select booking from Booking booking " +
            "where booking.end < ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByBookerPast(long userId, LocalDateTime end, Pageable pageable);

    @Query("select booking from Booking booking " +
            "where booking.start > ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByBookerFuture(long userId, LocalDateTime start, Pageable pageable);

    @Query("select booking from Booking booking " +
            "where booking.status = ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.status desc")
    List<Booking> findByBookerAndStatus(long userId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId, Pageable pageable);

    @Query("select booking from Booking booking " +
            "where booking.start < ?2 " +
            "and booking.end > ?2 " +
            "and booking.item.owner.id = ?1 " +
            "order by booking.start")
    List<Booking> findByItemOwnerCurrent(long userId, LocalDateTime now, Pageable pageable);

    @Query("select booking from Booking booking " +
            "where booking.end < ?2 " +
            "and booking.item.owner.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByItemOwnerPast(long userId, LocalDateTime end, Pageable pageable);

    @Query("select booking from Booking booking " +
            "where booking.start > ?2 " +
            "and booking.item.owner.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByItemOwnerFuture(long userId, LocalDateTime start, Pageable pageable);

    @Query("select booking from Booking booking " +
            "where booking.status = ?2 " +
            "and booking.item.owner.id = ?1 " +
            "order by booking.status desc")
    List<Booking> findByItemOwnerAndStatus(long userId, BookingStatus status, Pageable pageable);

    Optional<Booking> findByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime end);

    @Query("select distinct booking " +
            "from Booking booking " +
            "where booking.end < :now " +
            "and booking.item.id in :ids " +
            "and booking.item.owner.id = :userId " +
            "order by booking.start asc ")
    List<Booking> findBookingsLast(@Param("ids") List<Long> ids,
                                   @Param("now") LocalDateTime now,
                                   @Param("userId") long userId);

    @Query("select distinct booking " +
            "from Booking booking " +
            "where booking.start > :now " +
            "and booking.item.id in :ids " +
            "and booking.item.owner.id = :userId " +
            "order by booking.start asc ")
    List<Booking> findBookingsNext(@Param("ids") List<Long> ids,
                                   @Param("now") LocalDateTime now,
                                   @Param("userId") long userId);
}