package ru.practicum.shareit.booking.finding;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;

import java.awt.print.Pageable;
import java.util.List;

public interface FindBookingStrategy {
     boolean shouldBeRun(String status);

    Page<Booking> find(long userId, PageRequest pageRequest);

}
