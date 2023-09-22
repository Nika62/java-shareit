package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
											  @RequestParam(name = "state", defaultValue = "all") String stateParam,
											  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
											  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
														@RequestParam(name = "state", defaultValue = "all") String stateParam,
														@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
														@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

		checkValueFromAndSize(from, size);

		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookingsByOwner(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
												@RequestBody @Valid BookingDtoCreate bookingDtoCreate) {
		log.info("Creating booking {}, userId={}", bookingDtoCreate, userId);
		return bookingClient.createBooking(userId, bookingDtoCreate);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBooking(@PathVariable long bookingId, @RequestParam Boolean approved,
												@RequestHeader("X-Sharer-User-Id") long userId) {
		log.info("Update bookingId={}, userId={}, approved={}", bookingId, userId, approved);
		return bookingClient.updateBooking(bookingId, approved, userId);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
											 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	private void checkValueFromAndSize(int from, int size) {
		if (from < 0 || size <= 0) {
			log.error("Invalid value from={} or size={}", from, size);
			throw new ValidationException("Параметры запроса from = " + from + " или size = " + size + " введены некорректно");
		}
	}
}
