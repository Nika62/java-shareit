package ru.practicum.shareit.booking.finding.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.finding.FindBookingByOwnerStrategy;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindByOwnerWithoutStatus implements FindBookingByOwnerStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public boolean shouldBeRun(String status) {
        return status.equals("ALL");
    }

    @Override
    public List<Booking> find(long userId) {
        return bookingRepository.findAllByOwner(userId);
    }
}
