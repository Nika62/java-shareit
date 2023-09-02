package ru.practicum.shareit.booking.finding.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.finding.FindBookingByOwnerStrategy;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;
import static ru.practicum.shareit.booking.ResponseState.FUTURE;

@Component
@RequiredArgsConstructor
public class FindByOwnerWithFuture implements FindBookingByOwnerStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public boolean shouldBeRun(String status) {
        return status.equals(FUTURE.name());
    }

    @Override
    public Page<Booking> find(long userId, PageRequest pageRequest) {
        List<String> statuses = new ArrayList<>();
        statuses.add(String.valueOf(WAITING));
        statuses.add(String.valueOf(APPROVED));
        return bookingRepository.findAllByOwnerIdAndBookingFuture(userId, statuses, LocalDateTime.now(), pageRequest);

    }
}
