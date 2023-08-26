package ru.practicum.shareit.booking.finding.all;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.finding.FindBookingStrategy;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@RequiredArgsConstructor
@Component
public class FindAllWhitPast implements FindBookingStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public boolean shouldBeRun(String status) {
        return  status.equals("PAST");
    }

    @Override
    public List<Booking> find(long userId) {
        List<String> statuses = new ArrayList<>();
        statuses.add(String.valueOf(APPROVED));
        return bookingRepository.findAllByUserIdAndBookingPast(userId, statuses, LocalDateTime.now());

    }
}