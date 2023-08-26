package ru.practicum.shareit.booking.finding.all;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.finding.FindBookingStrategy;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Component
@RequiredArgsConstructor
public class FindAllWithWaiting implements FindBookingStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public boolean shouldBeRun(String status) {

      return  status.equals(WAITING.name());
    }

    @Override
    public List<Booking> find(long userId) {

        return bookingRepository.findAllByUserIdAndBookingStatus(userId, WAITING.name());

    }
}