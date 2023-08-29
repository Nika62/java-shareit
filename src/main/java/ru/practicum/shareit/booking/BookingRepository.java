package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking  b where b.id =?1 and (b.booker.id =?2  or b.item.user.id =?2)")
    Optional<Booking> findBookingByIdAndByUserId(long bookingId, long userId);


    @Query("select b from Booking b where  b.status in ?2 and b.booker.id =?1 and b.start >=?3 ORDER BY b.id DESC")
    List<Booking> findAllByUserIdAndBookingFuture(long userId, List<String> status, LocalDateTime localDateTime);


    @Query("select b from Booking b where  b.status in ?2 and b.booker.id =?1 and b.end <= ?3 ORDER BY b.id DESC")
    List<Booking> findAllByUserIdAndBookingPast(long userId, List<String> status, LocalDateTime localDateTime);


    @Query("select b from Booking b where  b.status in ?2 and b.booker.id =?1 and b.start <=?3 AND b.end >= ?3 ORDER BY b.id")
    List<Booking> findAllByUserIdAndBookingCurrent(long userId, List<String> status, LocalDateTime localDateTime);


    @Query("select b from Booking b where  b.status  =?2 and b.booker.id =?1 ORDER BY b.id DESC")
    List<Booking> findAllByUserIdAndBookingStatus(long userId, String status);


    @Query("select b from Booking b where (b.booker.id =?1 or b.item.user.id =?1) ORDER BY b.id DESC")
    List<Booking> findAllByBookerIdOrByItemUserId(long userId);


    @Query("select b from Booking b where b.item.user.id =?1 ORDER BY b.id DESC")
    List<Booking> findAllByOwner(long ownerId);


    @Query("select b from Booking b where  b.status =?2 and b.item.user.id =?1 ORDER BY b.id DESC")
    List<Booking> findAllByOwnerIdAndBookingStatus(long userId, String status);


    @Query("select b from Booking b where  b.status IN ?2 and b.item.user.id =?1 and b.start >=?3 ORDER BY b.id DESC")
    List<Booking> findAllByOwnerIdAndBookingFuture(long userId, List<String> status, LocalDateTime localDateTime);


    @Query("select b from Booking b where  b.status IN ?2 and b.item.user.id =?1 and b.end <=?3 ORDER BY b.id DESC")
    List<Booking> findAllByOwnerIdAndBookingPast(long userId, List<String> status, LocalDateTime localDateTime);


    @Query("select b from Booking b where  b.status IN ?2 and b.item.user.id =?1 and b.start <=?3 AND b.end >=?3 ORDER BY b.id DESC")
    List<Booking> findAllByOwnerIdAndBookingCurrent(long userId, List<String> status, LocalDateTime localDateTime);


    @Query("select b from Booking b where b.booker.id =?1 and b.item.id =?2 and b.status='APPROVED' and b.end <=?3")
    List<Booking> getByUserIdAndItemIdStatusApproved(long userId, long itemId, LocalDateTime localDateTime);


    @Query("select b from Booking b where b.item.id =?1  And b.end <= ?2 ORDER BY b.end Desc")
    List<Booking> getLastBooking(long itemId, LocalDateTime today);


    @Query("select b from Booking b where b.item.id =?1 And b.item.user.id =?2 And b.start >=?3 and b.status='APPROVED' ORDER BY b.end")
    List<Booking> getNextBooking(long itemId, long userId, LocalDateTime localDateTime);

}
