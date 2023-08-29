package ru.practicum.shareit.booking.finding;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface FindBookingStrategy {
     boolean shouldBeRun(String status);

     List<Booking> find(long userId);

}
