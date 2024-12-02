package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.general.LastNextBookingDates;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingJpaRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdAndStatusEqualsOrderByStart(Long userId, String status);

    List<Booking> findByBookerIdOrderByStart(Long userId);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusEqualsAndEndBefore(
            Long userId,
            Long itemId,
            BookingStatus status,
            LocalDateTime now);

    @Query("select bk from Booking as bk" +
            " where bk.booker.id = :userId" +
            " and bk.status = 'APPROVED'" +
            " and bk.start < :now" +
            " and bk.end >= :now")
    List<Booking> findApprovedCurrentUserBookings(@Param("userId") Long userId,
                                                  @Param("now") LocalDateTime now);

    @Query("select bk from Booking as bk" +
            " where bk.booker.id = :userId" +
            " and bk.status = 'APPROVED'" +
            " and bk.start < :now" +
            " and bk.end < :now")
    List<Booking> findApprovedPastUserBookings(@Param("userId") Long userId,
                                               @Param("now") LocalDateTime now);

    @Query("select bk from Booking as bk" +
            " where bk.booker.id = :userId" +
            " and bk.status = 'APPROVED'" +
            " and bk.start >= :now")
    List<Booking> findApprovedFutureUserBookings(@Param("userId") Long userId,
                                                 @Param("now") LocalDateTime now);

    List<Booking> findByItemOwnerOrderByStart(Long userId);

    List<Booking> findByItemOwnerAndStatusEqualsOrderByStart(Long userId, String status);

    @Query("select bk from Booking as bk" +
            " where bk.item.owner = :userId" +
            " and bk.status = 'APPROVED'" +
            " and bk.start < :now" +
            " and bk.end >= :now")
    List<Booking> findCurrentByItemOwner(@Param("userId") Long userId,
                                         @Param("now") LocalDateTime now);

    @Query("select bk from Booking as bk" +
            " where bk.item.owner = :userId" +
            " and bk.status = 'APPROVED'" +
            " and bk.start < :now" +
            " and bk.end < :now")
    List<Booking> findPastByItemOwner(@Param("userId") Long userId,
                                      @Param("now") LocalDateTime now);

    @Query("select bk from Booking as bk" +
            " where bk.item.owner = :userId" +
            " and bk.status = 'APPROVED'" +
            " and bk.start > :now")
    List<Booking> findFutureByItemOwner(@Param("userId") Long userId,
                                        @Param("now") LocalDateTime now);


    @Query("select new ru.practicum.shareit.general.LastNextBookingDates(max(bk.start), min(bk2.start))" +
            " FROM Item as it " +
            " left join Booking as bk ON (bk.item.id = it.id and bk.status = 'APPROVED' and bk.start <= :now)" +
            " left join Booking as bk2 ON (bk2.item.id = it.id and bk2.status = 'APPROVED' and bk2.start > :now)" +
            " where it.id = :itemId GROUP BY it.id")
    LastNextBookingDates getLastNextBookingDatesByItemId(@Param("itemId") Long itemId,
                                                         @Param("now") LocalDateTime now);
}
