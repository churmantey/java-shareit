package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.general.LastNextBookingDates;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdAndStatusEqualsOrderByStart(Long userId, String status);

    List<Booking> findByBookerIdOrderByStart(Long userId);

    @Query("select bk from Booking as bk" +
            " where bk.booker.id = :userId" +
            " and bk.status = 'APPROVED'" +
            " and bk.start < :currentTime" +
            " and bk.end >= :currentTime")
    List<Booking> findApprovedCurrentUserBookings (@Param("userId") Long userId,
                                            @Param("currentTime") LocalDateTime currentTime);

    @Query("select bk from Booking as bk" +
            " where bk.booker.id = :userId" +
            " and bk.status = 'APPROVED'" +
            " and bk.start < :currentTime" +
            " and bk.end < :currentTime")
    List<Booking> findApprovedPastUserBookings (@Param("userId") Long userId,
                                                   @Param("currentTime") LocalDateTime currentTime);

    @Query("select bk from Booking as bk" +
            " where bk.booker.id = :userId" +
            " and bk.status = 'APPROVED'" +
            " and bk.start >= :currentTime")
    List<Booking> findApprovedFutureUserBookings (@Param("userId") Long userId,
                                                @Param("currentTime") LocalDateTime currentTime);

    List<Booking> findByItemOwnerOrderByStart (Long userId);

    List<Booking> findByItemOwnerAndStatusEqualsOrderByStart(Long userId, String status);

    @Query("select bk from Booking as bk" +
            " where bk.item.owner = :userId" +
            " and bk.status = 'APPROVED'" +
            " and bk.start < :currentTime" +
            " and bk.end >= :currentTime")
    List<Booking> findCurrentByItemOwner (@Param("userId") Long userId,
                                                   @Param("currentTime") LocalDateTime currentTime);

    @Query("select bk from Booking as bk" +
            " where bk.item.owner = :userId" +
            " and bk.status = 'APPROVED'" +
            " and bk.start < :currentTime" +
            " and bk.end < :currentTime")
    List<Booking> findPastByItemOwner (@Param("userId") Long userId,
                                          @Param("currentTime") LocalDateTime currentTime);

    @Query("select bk from Booking as bk" +
            " where bk.item.owner = :userId" +
            " and bk.status = 'APPROVED'" +
            " and bk.start > :currentTime")
    List<Booking> findFutureByItemOwner (@Param("userId") Long userId,
                                       @Param("currentTime") LocalDateTime currentTime);


    @Query("select new ru.practicum.shareit.general.LastNextBookingDates(max(bk.start), min(bk2.start))" +
            " FROM Item as it " +
            " left join Booking as bk ON (bk.item.id = it.id and bk.status = 'APPROVED' and bk.start <= :currentTime)" +
            " left join Booking as bk2 ON (bk2.item.id = it.id and bk2.status = 'APPROVED' and bk2.start > :currentTime)" +
            " where it.id = :itemId GROUP BY it.id")
    LastNextBookingDates getLastNextBookingDatesByItemId(@Param("itemId") Long itemId,
                                                         @Param("currentTime") LocalDateTime currentTime);
}
