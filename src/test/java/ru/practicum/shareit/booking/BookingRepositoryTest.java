package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.HelperCreationEntities.*;
import static ru.practicum.shareit.booking.BookingStatus.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private Booking booking = getBooking();
    private Booking futureBookingApprove;

    private Booking futureBookingWaiting;
    private Booking pastBooking1;
    private Booking pastBooking2;
    private Booking returnedBooking;

    private Booking currentBookingApproved;
    private Booking currentBookingRejected;
    private User owner;
    private Item item;
    private User booker;

    @BeforeEach
    public void before() {
        owner = userRepository.save(getUser());
        booker = userRepository.save(getUser2());
        item = itemRepository.save(getItem());
        booking.setBooker(booker);
        booking.setItem(item);
        returnedBooking = bookingRepository.save(booking);
        futureBookingApprove = bookingRepository.save(new Booking(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), item, booker, APPROVED.name()));
        futureBookingWaiting = bookingRepository.save(new Booking(LocalDateTime.now().plusHours(2), LocalDateTime.now().plusDays(2), item, booker, WAITING.name()));
        pastBooking1 = bookingRepository.save(new Booking(LocalDateTime.of(2023, 9, 1, 10, 10, 10), LocalDateTime.of(2023, 9, 2, 10, 10, 10), item, booker, APPROVED.name()));
        pastBooking2 = bookingRepository.save(new Booking(LocalDateTime.of(2023, 8, 1, 10, 10, 10), LocalDateTime.of(2023, 8, 2, 10, 10, 10), item, booker, APPROVED.name()));
        currentBookingApproved = bookingRepository.save(new Booking(LocalDateTime.of(2023, 10, 7, 10, 10, 10), LocalDateTime.of(2023, 10, 8, 22, 10, 10), item, booker, APPROVED.name()));
        currentBookingRejected = bookingRepository.save(new Booking(LocalDateTime.of(2023, 10, 7, 7, 10, 10), LocalDateTime.of(2023, 10, 8, 21, 10, 10), item, booker, REJECTED.name()));

    }

    @Test
    void findBookingByIdAndByBookerId() {
        Booking findBooking = bookingRepository.findBookingByIdAndByUserId(1, 2).get();
        assertEquals(returnedBooking.getId(), findBooking.getId());
        assertEquals(findBooking.getBooker().getId(), 2);

    }

    @Test
    void findBookingByIdAndByOwnerId() {
        Booking findBooking = bookingRepository.findBookingByIdAndByUserId(1, 1).get();
        assertEquals(returnedBooking.getId(), findBooking.getId());
        assertEquals(findBooking.getItem().getUser().getId(), 1);

    }

    @Test
    void findAllByUserIdAndBookingFuturesStatus() {
        PageRequest pageRequest = PageRequest.of(0 / 3, 3);
        List<Booking> bookings = bookingRepository.findAllByUserIdAndBookingFuture(2, List.of(WAITING.name(), APPROVED.name()), LocalDateTime.now(), pageRequest).toList();
        assertEquals(bookings.size(), 3);
        assertEquals(bookings.get(1), futureBookingWaiting);
        assertEquals(bookings.get(2), futureBookingApprove);
    }

    @Test
    void findAllByUserIdAndBookingFuturesStatusSize1() {
        PageRequest pageRequest = PageRequest.of(0 / 1, 1);
        List<Booking> bookings = bookingRepository.findAllByUserIdAndBookingFuture(2, List.of(WAITING.name(), APPROVED.name()), LocalDateTime.now(), pageRequest).toList();
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), currentBookingApproved);
    }

    @Test
    void findAllByUserIdAndBookingPast() {
        PageRequest pageRequest = PageRequest.of(0 / 10, 10);
        List<Booking> bookings = bookingRepository.findAllByUserIdAndBookingPast(2, List.of(APPROVED.name()), LocalDateTime.now(), pageRequest).toList();
        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0), pastBooking2);
    }

    @Test
    void findAllByUserIdAndBookingPastSize1() {
        PageRequest pageRequest = PageRequest.of(0 / 1, 1);
        List<Booking> bookings = bookingRepository.findAllByUserIdAndBookingPast(2, List.of(APPROVED.name()), LocalDateTime.now(), pageRequest).toList();
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), pastBooking2);
    }

    @Test
    void findAllByUserIdAndBookingCurrent() {
        PageRequest pageRequest = PageRequest.of(0 / 3, 3);
        List<Booking> bookings = bookingRepository.findAllByUserIdAndBookingCurrent(2, List.of(APPROVED.name(), REJECTED.name()), LocalDateTime.of(2023, 10, 7, 20, 20, 20), pageRequest).toList();
        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0), currentBookingApproved);
        assertEquals(bookings.get(1), currentBookingRejected);
    }

    @Test
    void findAllByUserIdAndBookingCurrentSize1() {
        PageRequest pageRequest = PageRequest.of(0 / 1, 1);
        List<Booking> bookings = bookingRepository.findAllByUserIdAndBookingCurrent(2, List.of(APPROVED.name(), REJECTED.name()), LocalDateTime.of(2023, 10, 7, 20, 20, 20), pageRequest).toList();
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), currentBookingApproved);
    }

    @Test
    void findAllByUserIdAndBookingStatus() {
        PageRequest pageRequest = PageRequest.of(0 / 10, 10);
        List<Booking> bookings = bookingRepository.findAllByUserIdAndBookingStatus(2, APPROVED.name(), pageRequest).toList();
        assertEquals(bookings.size(), 4);
        assertEquals(bookings.get(0), currentBookingApproved);
    }

    @Test
    void findAllByBookerIdOrderByIdDesc() {
        PageRequest pageRequest = PageRequest.of(0 / 10, 10);
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByIdDesc(2, pageRequest).toList();
        assertEquals(bookings.size(), 7);
        assertEquals(bookings.get(0), currentBookingRejected);
    }

    @Test
    void findAllByOwner() {
        PageRequest pageRequest = PageRequest.of(0 / 10, 10);
        List<Booking> bookings = bookingRepository.findAllByOwner(1, pageRequest).toList();
        assertEquals(bookings.size(), 7);
        assertEquals(bookings.get(0), currentBookingRejected);
    }

    @Test
    void findAllByOwnerIdAndBookingStatus() {
        PageRequest pageRequest = PageRequest.of(0 / 10, 10);
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndBookingStatus(1, WAITING.name(), pageRequest).toList();
        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0), futureBookingWaiting);
    }

    @Test
    void findAllByOwnerIdAndBookingFuture() {
        PageRequest pageRequest = PageRequest.of(0 / 3, 3);
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndBookingFuture(1, List.of(WAITING.name(), APPROVED.name()), LocalDateTime.now(), pageRequest).toList();
        assertEquals(bookings.size(), 3);
        assertEquals(bookings.get(1), futureBookingWaiting);
        assertEquals(bookings.get(2), futureBookingApprove);
    }

    @Test
    void findAllByOwnerIdAndBookingPast() {
        PageRequest pageRequest = PageRequest.of(0 / 3, 3);
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndBookingPast(1, List.of(APPROVED.name()), LocalDateTime.now(), pageRequest).toList();
        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0), pastBooking2);
    }

    @Test
    void findAllByOwnerIdAndBookingCurrent() {
        PageRequest pageRequest = PageRequest.of(0 / 3, 3);
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndBookingCurrent(1, List.of(APPROVED.name(), REJECTED.name()), LocalDateTime.of(2023, 10, 7, 20, 20, 20), pageRequest).toList();
        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0), currentBookingRejected);
        assertEquals(bookings.get(1), currentBookingApproved);
    }

    @Test
    void findAllByOwnerIdAndBookingCurrentSize1() {
        PageRequest pageRequest = PageRequest.of(0 / 1, 1);
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndBookingCurrent(1, List.of(APPROVED.name(), REJECTED.name()), LocalDateTime.of(2023, 10, 7, 20, 20, 20), pageRequest).toList();
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), currentBookingRejected);
    }

    @Test
    void getByUserIdAndItemIdStatusApproved() {
        List<Booking> bookings = bookingRepository.getByUserIdAndItemIdStatusApproved(2, 1, LocalDateTime.of(2023, 9, 2, 10, 11, 10));
        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0), pastBooking1);
    }

    @Test
    void getLastBooking() {
        List<Booking> bookings = bookingRepository.getLastBooking(1, LocalDateTime.of(2023, 9, 2, 10, 11, 10));
        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0), pastBooking1);
    }

    @Test
    void getNextBooking() {
        List<Booking> bookings = bookingRepository.getNextBooking(1, 1, LocalDateTime.now());
        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0), futureBookingApprove);
    }
}