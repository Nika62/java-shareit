package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    private final static String STATE_ALL = "ALL";
    private final static String DEFAULT_SIZE = "10";
    private final static String DEFAULT_FROM = "0";

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @Valid
                                    @RequestBody BookingDtoCreate bookingDtoCreate) {
        return bookingService.createBooking(userId, bookingDtoCreate);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable long bookingId, @RequestParam Boolean approved,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.updateBooking(bookingId, approved, userId);
    }

    @GetMapping ("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    List<BookingDto> getAllBookingsByUserIdAndStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(defaultValue = STATE_ALL) String state,
                                                     @RequestParam(defaultValue = DEFAULT_FROM) int from,
                                                     @RequestParam(defaultValue = DEFAULT_SIZE) int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Параметры запроса from = " + from + " или size = " + size + " введены некорректно");
        }
        return bookingService.getAllBookingsByUserIdAndStatus(userId, state, from, size);
    }

    @GetMapping("/owner")
    List<BookingDto> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(defaultValue = STATE_ALL) String state,
                                           @RequestParam(defaultValue = DEFAULT_FROM) int from,
                                           @RequestParam(defaultValue = DEFAULT_SIZE) int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Параметры запроса from = " + from + " или size = " + size + " введены некорректно");
        }
        return bookingService.getAllBookingsByOwnerAndStatus(userId, state, from, size);
    }

}