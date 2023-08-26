package ru.practicum.shareit.booking.finding.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.finding.FindBookingByOwnerStrategy;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.REJECTED;

@RequiredArgsConstructor
@Component
public class FindByOwnerCurrent implements FindBookingByOwnerStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public boolean shouldBeRun(String status) {
        return  status.equals("CURRENT");
    }

    @Override
    public List<Booking> find(long userId) {
        List<String> statuses = new ArrayList<>();
        statuses.add(String.valueOf(REJECTED));
        statuses.add(String.valueOf(APPROVED));
        return bookingRepository.findAllByOwnerIdAndBookingCurrent(userId, statuses, LocalDateTime.now());

    }
}
