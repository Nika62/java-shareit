package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.finding.FindBookingByOwnerStrategy;
import ru.practicum.shareit.booking.finding.FindBookingStrategy;
import ru.practicum.shareit.exception.ObjectUnavailableException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.HelperCreationEntities.*;
import static ru.practicum.shareit.booking.BookingStatus.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingServiceImplIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private List<FindBookingStrategy> strategies;
    @Autowired
    private List<FindBookingByOwnerStrategy> strategiesForOwner;

    @BeforeEach
    public void before() {
        userRepository.save(getUser());
        userRepository.save(getUser2());
        itemRepository.save(getItem());
    }

    @Test
    public void shouldCreateBooking() {
        BookingDto bookingDto = bookingService.createBooking(2, new BookingDtoCreate(getBookingAfterCreate().getStart(), getBookingAfterCreate().getEnd(), 1));
        assertEquals(bookingDto.getId(), 1);
        assertEquals(bookingDto.getItem().getId(), 1);
        assertEquals(bookingDto.getBooker().getId(), 2);
        assertEquals(bookingDto.getStatus(), WAITING);
    }

    @Test
    public void shouldCreateBookingItemNotExist() {
        Item item = itemRepository.save(new Item("item name", "item description", false, new User(1L, "user name", "user@mail.com")));
        Exception e = assertThrows(ObjectUnavailableException.class,
                () -> {
                    bookingService.createBooking(2, new BookingDtoCreate(getBookingAfterCreate().getStart(), getBookingAfterCreate().getEnd(), 2));
                });
        assertEquals("Вещь " + item + " недоступна для бронирования", e.getMessage());
    }


    @Test
    public void shouldUpdateBooking() {
        bookingService.createBooking(2, new BookingDtoCreate(getBookingAfterCreate().getStart(), getBookingAfterCreate().getEnd(), 1));
        BookingDto bookingDto = bookingService.updateBooking(1, true, 1);
        assertEquals(bookingDto.getId(), 1);
        assertEquals(bookingDto.getStatus(), APPROVED);
    }

    @Test
    public void shouldGetBookingById() {
        bookingService.createBooking(2, new BookingDtoCreate(getBookingAfterCreate().getStart(), getBookingAfterCreate().getEnd(), 1));
        BookingDto bookingDto = bookingService.getBookingById(1, 1);
        assertEquals(bookingDto.getId(), 1);
        assertEquals(bookingDto.getBooker().getId(), 2);
        assertEquals(bookingDto.getItem().getId(), 1);
        assertNotNull(bookingDto.getStart());
        assertNotNull(bookingDto.getEnd());
        assertEquals(bookingDto.getStatus(), WAITING);
    }

    @Test
    public void shouldGetAllBookingsByUserIdAndStatusPast() {
        bookingService.createBooking(2, new BookingDtoCreate(getBookingAfterCreate().getStart(), getBookingAfterCreate().getEnd(), 1));
        bookingService.updateBooking(1, true, 1);
        List<BookingDto> bookings = bookingService.getAllBookingsByUserIdAndStatus(2, "PAST", 0, 3);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStart(), getBookingAfterCreate().getStart());
        assertEquals(bookings.get(0).getEnd(), getBookingAfterCreate().getEnd());
        assertEquals(bookings.get(0).getStatus(), APPROVED);
    }

    @Test
    public void shouldGetAllBookingsByUserIdAndStatusFuture() {
        bookingService.createBooking(2, new BookingDtoCreate(LocalDateTime.of(2023, 12, 12, 12, 12, 1), LocalDateTime.of(2023, 12, 13, 12, 12, 1), 1));
        bookingService.updateBooking(1, true, 1);
        List<BookingDto> bookings = bookingService.getAllBookingsByUserIdAndStatus(2, "FUTURE", 0, 3);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), APPROVED);
    }

    @Test
    public void shouldGetAllBookingsByUserIdAndStatusRejected() {
        bookingService.createBooking(2, new BookingDtoCreate(LocalDateTime.of(2023, 12, 12, 12, 12, 1), LocalDateTime.of(2023, 12, 13, 12, 12, 1), 1));
        bookingService.updateBooking(1, false, 1);
        List<BookingDto> bookings = bookingService.getAllBookingsByUserIdAndStatus(2, "REJECTED", 0, 3);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), REJECTED);
    }

    @Test
    public void shouldGetAllBookingsByUserIdAndStatusCurrent() {
        bookingService.createBooking(2, new BookingDtoCreate(LocalDateTime.now(), LocalDateTime.now().plusHours(2), 1));
        bookingService.updateBooking(1, true, 1);
        List<BookingDto> bookings = bookingService.getAllBookingsByUserIdAndStatus(2, "CURRENT", 0, 3);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), APPROVED);
    }

    @Test
    public void shouldGetAllBookingsByUserIdAndStatusAll() {
        bookingService.createBooking(2, new BookingDtoCreate(LocalDateTime.now(), LocalDateTime.now().plusHours(2), 1));
        bookingService.createBooking(2, new BookingDtoCreate(LocalDateTime.of(2023, 12, 12, 12, 12, 1), LocalDateTime.of(2023, 12, 13, 12, 12, 1), 1));
        List<BookingDto> bookings = bookingService.getAllBookingsByUserIdAndStatus(2, "ALL", 0, 3);
        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0).getId(), 2);
        assertEquals(bookings.get(1).getId(), 1);
    }

    @Test
    public void shouldGetAllBookingsByOwnerAndStatusPast() {
        bookingService.createBooking(2, new BookingDtoCreate(getBookingAfterCreate().getStart(), getBookingAfterCreate().getEnd(), 1));
        bookingService.updateBooking(1, true, 1);
        List<BookingDto> bookings = bookingService.getAllBookingsByOwnerAndStatus(1, "PAST", 0, 3);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStart(), getBookingAfterCreate().getStart());
        assertEquals(bookings.get(0).getEnd(), getBookingAfterCreate().getEnd());
        assertEquals(bookings.get(0).getStatus(), APPROVED);
    }

    @Test
    public void shouldGetAllBookingsByOwnerIdAndStatusFuture() {
        bookingService.createBooking(2, new BookingDtoCreate(LocalDateTime.of(2023, 12, 12, 12, 12, 1), LocalDateTime.of(2023, 12, 13, 12, 12, 1), 1));
        bookingService.updateBooking(1, true, 1);
        List<BookingDto> bookings = bookingService.getAllBookingsByOwnerAndStatus(1, "FUTURE", 0, 3);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), APPROVED);
    }

    @Test
    public void shouldGetAllBookingsByOwnerIdAndStatusRejected() {
        bookingService.createBooking(2, new BookingDtoCreate(LocalDateTime.of(2023, 12, 12, 12, 12, 1), LocalDateTime.of(2023, 12, 13, 12, 12, 1), 1));
        bookingService.updateBooking(1, false, 1);
        List<BookingDto> bookings = bookingService.getAllBookingsByOwnerAndStatus(1, "REJECTED", 0, 3);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), REJECTED);
    }

    @Test
    public void shouldGetAllBookingsByOwnerIdAndStatusCurrent() {
        bookingService.createBooking(2, new BookingDtoCreate(LocalDateTime.now(), LocalDateTime.now().plusHours(2), 1));
        bookingService.updateBooking(1, true, 1);
        List<BookingDto> bookings = bookingService.getAllBookingsByOwnerAndStatus(1, "CURRENT", 0, 3);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), APPROVED);
    }

    @Test
    public void shouldGetAllBookingsByOwnerIdAndStatusAll() {
        bookingService.createBooking(2, new BookingDtoCreate(LocalDateTime.now(), LocalDateTime.now().plusHours(2), 1));
        bookingService.createBooking(2, new BookingDtoCreate(LocalDateTime.of(2023, 12, 12, 12, 12, 1), LocalDateTime.of(2023, 12, 13, 12, 12, 1), 1));
        List<BookingDto> bookings = bookingService.getAllBookingsByOwnerAndStatus(1, "ALL", 0, 3);
        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0).getId(), 2);
        assertEquals(bookings.get(1).getId(), 1);
    }

}
