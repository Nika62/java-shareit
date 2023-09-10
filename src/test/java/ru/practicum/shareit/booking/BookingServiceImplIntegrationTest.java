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
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.shareit.HelperCreationEntities.*;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

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
    public void shouldGetAllBookingsByUserIdAndStatus() {
        bookingService.createBooking(2, new BookingDtoCreate(getBookingAfterCreate().getStart(), getBookingAfterCreate().getEnd(), 1));
        bookingService.updateBooking(1, true, 1);
        List<BookingDto> bookings = bookingService.getAllBookingsByUserIdAndStatus(2, "PAST", 0, 3);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStart(), getBookingAfterCreate().getStart());
        assertEquals(bookings.get(0).getEnd(), getBookingAfterCreate().getEnd());
        assertEquals(bookings.get(0).getStatus(), APPROVED);
    }

    @Test
    public void shouldGetAllBookingsByOwnerAndStatus() {
        bookingService.createBooking(2, new BookingDtoCreate(getBookingAfterCreate().getStart(), getBookingAfterCreate().getEnd(), 1));
        bookingService.updateBooking(1, true, 1);
        List<BookingDto> bookings = bookingService.getAllBookingsByOwnerAndStatus(1, "PAST", 0, 3);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStart(), getBookingAfterCreate().getStart());
        assertEquals(bookings.get(0).getEnd(), getBookingAfterCreate().getEnd());
        assertEquals(bookings.get(0).getStatus(), APPROVED);
    }

}
