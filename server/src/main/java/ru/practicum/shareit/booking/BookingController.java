package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    private final String stateAll = "ALL";
    private final String defaultFrom = "0";
    private final String defaultSize = "10";

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestBody BookingDtoCreate bookingDtoCreate) {
        return bookingService.createBooking(userId, bookingDtoCreate);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable long bookingId, @RequestParam Boolean approved,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.updateBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    List<BookingDto> getAllBookingsByUserIdAndStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(defaultValue = stateAll) String state,
                                                     @RequestParam(defaultValue = defaultFrom) int from,
                                                     @RequestParam(defaultValue = defaultSize) int size) {

        return bookingService.getAllBookingsByUserIdAndStatus(userId, state, from, size);
    }

    @GetMapping("/owner")
    List<BookingDto> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(defaultValue = stateAll) String state,
                                           @RequestParam(defaultValue = defaultFrom) int from,
                                           @RequestParam(defaultValue = defaultSize) int size) {

        return bookingService.getAllBookingsByOwnerAndStatus(userId, state, from, size);
    }

}