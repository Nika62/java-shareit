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
    @Query("select b from Booking  b where b.id =:bookingId and (b.booker.id =:userId or b.item.user.id =:userId)")
    Optional<Booking> findBookingByIdAndByUserId(long bookingId, long userId);
    @Query("select b from Booking b where  b.status in :status and b.booker.id =:userId and b.start >= :localDateTime ORDER BY b.id DESC")
    List<Booking> findAllByUserIdAndBookingFuture(long userId, List<String> status, LocalDateTime localDateTime);

    @Query("select b from Booking b where  b.status in :status and b.booker.id =:userId and b.end <= :localDateTime ORDER BY b.id DESC")
    List<Booking> findAllByUserIdAndBookingPast(long userId, List<String> status, LocalDateTime localDateTime);

    @Query("select b from Booking b where  b.status in :status and b.booker.id =:userId and b.start <= :localDateTime AND b.end >= :localDateTime ORDER BY b.id")
    List<Booking> findAllByUserIdAndBookingCurrent(long userId, List<String> status, LocalDateTime localDateTime);
    @Query("select b from Booking b where  b.status  =:status and b.booker.id =:userId ORDER BY b.id DESC")
    List<Booking> findAllByUserIdAndBookingStatus(long userId,String status);

    @Query("select b from Booking b where (b.booker.id =:userId or b.item.user.id =:userId) ORDER BY b.id DESC")
    List<Booking> findAllByBookerIdOrByItemUserId(long userId);
   @Query("select b from Booking b where b.item.user.id =:ownerId ORDER BY b.id DESC")
    List<Booking> findAllByOwner(long ownerId);

    @Query("select b from Booking b where  b.status =:status and b.item.user.id =:userId ORDER BY b.id DESC")
    List<Booking> findAllByOwnerIdAndBookingStatus(long userId, String status);

    @Query("select b from Booking b where  b.status IN :status and b.item.user.id =:userId and b.start >= :localDateTime ORDER BY b.id DESC")
    List<Booking> findAllByOwnerIdAndBookingFuture(long userId, List<String> status, LocalDateTime localDateTime);

    @Query("select b from Booking b where  b.status IN :status and b.item.user.id =:userId and b.end <= :localDateTime ORDER BY b.id DESC")
    List<Booking> findAllByOwnerIdAndBookingPast(long userId, List<String> status, LocalDateTime localDateTime);

    @Query("select b from Booking b where  b.status IN :status and b.item.user.id =:userId and b.start <= :localDateTime AND b.end >= :localDateTime ORDER BY b.id DESC")
    List<Booking> findAllByOwnerIdAndBookingCurrent(long userId, List<String> status, LocalDateTime localDateTime);

    @Query("select b from Booking b where b.booker.id =:userId and b.item.id =:itemId and b.status='APPROVED' and b.end <= :localDateTime")
    List<Booking> getByUserIdAndItemIdStatusApproved(long userId, long itemId, LocalDateTime localDateTime);

    @Query("select b from Booking b where b.item.id =:itemId And b.item.user.id =:userId And b.end <= :localDateTime ORDER BY b.end DESC")
    List<Booking> getLastBooking(long itemId, long userId, LocalDateTime localDateTime);

    @Query("select b from Booking b where b.item.id =:itemId And b.item.user.id =:userId And b.start >= :localDateTime and b.status='APPROVED' ORDER BY b.end")
    List<Booking> getNextBooking(long itemId, long userId, LocalDateTime localDateTime);

}
