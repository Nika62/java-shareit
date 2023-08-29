package ru.practicum.shareit.booking.finding;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface FindBookingByOwnerStrategy {

    public boolean shouldBeRun(String status);

    public List<Booking> find(long userId);
}
