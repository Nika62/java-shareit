package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.finding.FindBookingByOwnerStrategy;
import ru.practicum.shareit.booking.finding.FindBookingStrategy;
import ru.practicum.shareit.booking.finding.all.FindAllWithWaiting;
import ru.practicum.shareit.booking.finding.owner.FindByOwnerCurrent;
import ru.practicum.shareit.booking.finding.owner.FindByOwnerWaiting;
import ru.practicum.shareit.booking.impl.BookingServiceImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ObjectUnavailableException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.HelperCreationEntities.*;
import static ru.practicum.shareit.booking.BookingStatus.*;
import static ru.practicum.shareit.booking.ResponseState.CURRENT;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;
    private UserMapper userMapper = new UserMapper();
    private ItemMapper itemMapper = new ItemMapper(userMapper);

    private BookingMapper bookingMapper = new BookingMapper(userMapper, itemMapper);

    private User booker;

    private Item item;

    private User owner;
    private Booking booking;

    private List<FindBookingStrategy> strategies = new ArrayList<>();
    private List<FindBookingByOwnerStrategy> strategiesForOwner = new ArrayList<>();

    @BeforeEach
    public void before() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository,
                bookingMapper, strategies, strategiesForOwner);

        booking = getBookingAfterCreate();
        booker = getUserALLField();
        item = getItemAllField();
        owner = new User(1L, "name", "name@email.ru");
    }

    @Test
    void createBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        BookingDto returnedBookingDto = bookingService.createBooking(2L, new BookingDtoCreate(getBookingAfterCreate().getStart(), getBookingAfterCreate().getEnd(), 1L));
        assertEquals(booking.getItem().getId(), returnedBookingDto.getItem().getId());
        assertEquals(booking.getBooker().getId(), returnedBookingDto.getBooker().getId());
        assertEquals(returnedBookingDto.getStatus(), WAITING);
    }

    @Test
    void shouldReturnExceptionCreateBookingBookerNotExist() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException("Пользователь с id = 99 не найден"));
        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.createBooking(99L, new BookingDtoCreate(getBookingAfterCreate().getStart(), getBookingAfterCreate().getEnd(), 1L));
                });
        assertEquals("Пользователь с id = 99 не найден", e.getMessage());
    }

    @Test
    void shouldReturnExceptionCreateBookingItemNotExist() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenThrow(new NotFoundException("Вещь с id = 99 не найдена"));

        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.createBooking(2L, new BookingDtoCreate(getBookingAfterCreate().getStart(), getBookingAfterCreate().getEnd(), 99L));
                });
        assertEquals("Вещь с id = 99 не найдена", e.getMessage());
    }

    @Test
    void shouldReturnExceptionCreateBookingOwnerEqualsBooker() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.createBooking(1L, new BookingDtoCreate(getBookingAfterCreate().getStart(), getBookingAfterCreate().getEnd(), 1L));
                });
        assertEquals("Вещь для бронирования не найдена", e.getMessage());
    }

    @Test
    void shouldReturnExceptionCreateBookingAvailableFalse() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Exception e = assertThrows(ObjectUnavailableException.class,
                () -> {
                    bookingService.createBooking(2L, new BookingDtoCreate(getBookingAfterCreate().getStart(), getBookingAfterCreate().getEnd(), 1L));
                });
        assertEquals("Вещь " + item + " недоступна для бронирования", e.getMessage());
    }

    @Test
    void shouldReturnExceptionCreateBookingWrongTime() {
        Exception e = assertThrows(ValidationException.class,
                () -> {
                    bookingService.createBooking(2L, new BookingDtoCreate(getBookingAfterCreate().getEnd(), getBookingAfterCreate().getStart(), 1L));
                });
        assertEquals("Дата окончания бронирования должна быть позже даты начала бронирования", e.getMessage());
    }

    @Test
    void updateBookingApproved() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDto updateBookingDto = bookingService.updateBooking(1, true, 1);

        assertEquals(updateBookingDto.getStatus(), APPROVED);

    }

    @Test
    void updateBookingRejected() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDto updateBookingDto = bookingService.updateBooking(1, false, 1);

        assertEquals(updateBookingDto.getStatus(), REJECTED);
    }

    @Test
    void updateBookingCanceled() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDto updateBookingDto = bookingService.updateBooking(1, false, 2);

        assertEquals(updateBookingDto.getStatus(), CANCELED);
    }

    @Test
    void shouldReturnExceptionUpdateBookingStatusBookerTrue() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.updateBooking(1, true, 2);
                });
        assertEquals("У пользователя с id = 2 нет прав для подтверждения бронирования", e.getMessage());
    }

    @Test
    void getBookingById() {
        when(bookingRepository.findBookingByIdAndByUserId(1L, 2L))
                .thenReturn(Optional.of(booking));

        BookingDto returnedBookingDto = bookingService.getBookingById(1, 2);
        assertEquals(returnedBookingDto.getId(), booking.getId());
    }

    @Test
    void shouldReturnExceptionBookingNotFound() {
        ;
        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.getBookingById(99, 199);
                });
        assertEquals("Бронирование с id = 99 для пользователя с id = 199 не найдено", e.getMessage());
    }

    @Test
    void getAllBookingsByUserIdAndStatus() {
        FindAllWithWaiting findAllWithWaiting = new FindAllWithWaiting(bookingRepository);
        strategies.add(findAllWithWaiting);
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        PageRequest pageRequest = PageRequest.of(0 / 3, 3);
        when(bookingRepository.findAllByUserIdAndBookingStatus(2, WAITING.name(), pageRequest))
                .thenReturn(bookings);
        List<BookingDto> bookingsDto = bookingService.getAllBookingsByUserIdAndStatus(2, WAITING.name(), 0, 3);
        assertEquals(bookingsDto.size(), 1);
        assertEquals(bookingsDto.get(0).getId(), booking.getId());
    }

    @Test
    void shouldReturnExceptionGetAllBookingsByUnknownStatus() {
        Exception e = assertThrows(ValidationException.class,
                () -> {
                    bookingService.getAllBookingsByUserIdAndStatus(2, "YES", 0, 3);
                });
        assertEquals("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    @Test
    void shouldReturnExceptionGetAllBookingsEmpty() {
        FindAllWithWaiting findAllWithWaiting = new FindAllWithWaiting(bookingRepository);
        strategies.add(findAllWithWaiting);
        Page<Booking> bookings = new PageImpl<>(new ArrayList<>());
        PageRequest pageRequest = PageRequest.of(0 / 3, 3);
        when(bookingRepository.findAllByUserIdAndBookingStatus(2, WAITING.name(), pageRequest))
                .thenReturn(bookings);
        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.getAllBookingsByUserIdAndStatus(2, WAITING.name(), 0, 3);
                });
        assertEquals("Не найдено ни одного подходящего бронирования", e.getMessage());
    }

    @Test
    void getAllBookingsByOwnerAndStatus() {
        FindByOwnerCurrent findByOwnerCurrent = new FindByOwnerCurrent(bookingRepository);
        strategiesForOwner.add(findByOwnerCurrent);
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        PageRequest pageRequest = PageRequest.of(0 / 3, 3);
        when(bookingRepository.findAllByOwnerIdAndBookingCurrent(anyLong(), anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookings);
        List<BookingDto> bookingsDto = bookingService.getAllBookingsByOwnerAndStatus(1, CURRENT.name(), 0, 3);
        assertEquals(bookingsDto.size(), 1);
        assertEquals(bookingsDto.get(0).getId(), booking.getId());
    }

    @Test
    void shouldReturnExceptionGetBookingsByOwnerEmpty() {
        FindByOwnerWaiting findWithWaiting = new FindByOwnerWaiting(bookingRepository);
        strategiesForOwner.add(findWithWaiting);
        Page<Booking> bookings = new PageImpl<>(new ArrayList<>());
        PageRequest pageRequest = PageRequest.of(0 / 3, 3);
        when(bookingRepository.findAllByOwnerIdAndBookingStatus(1, WAITING.name(), pageRequest))
                .thenReturn(bookings);
        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.getAllBookingsByOwnerAndStatus(1, WAITING.name(), 0, 3);
                });
        assertEquals("Не найдено ни одного подходящего бронирования", e.getMessage());
    }

    @Test
    void shouldReturnExceptionGetBookingsByOwnerByUnknownStatus() {
        Exception e = assertThrows(ValidationException.class,
                () -> {
                    bookingService.getAllBookingsByOwnerAndStatus(1, "YES", 0, 3);
                });
        assertEquals("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

}