package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.finding.FindBookingByOwnerStrategy;
import ru.practicum.shareit.booking.finding.FindBookingStrategy;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ObjectUnavailableException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final List<FindBookingStrategy> strategies;

    private final List<FindBookingByOwnerStrategy> strategiesForOwner;

    @Override
    public BookingDto createBooking(long userId, BookingDtoCreate bookingDtoCreate) {
        compareBookingStartEnd(bookingDtoCreate);
        User booker = getUserOrTrow(userId);
        Item item = getItemOrTrow(bookingDtoCreate.getItemId());

        if (item.getUser().getId() == userId) {
            throw new NotFoundException("Вещь для бронирования не найдена");
        }
        if (!item.isAvailable()) {
            throw new ObjectUnavailableException("Вещь " + item + " недоступна для бронирования");
        }

        Booking booking = bookingMapper.convertBookingDtoCreateToBooking(booker, item, bookingDtoCreate);
        booking.setStatus(WAITING.name());
        return bookingMapper.convertBookingToBookingDto(
                bookingRepository.save(booking));

    }

    @Override
    public BookingDto updateBooking(long bookingId, Boolean approved, long userId) {
        User user = getUserOrTrow(userId);
        Booking booking = getBookingOrTrow(bookingId);
        Item item = booking.getItem();

        if (booking.getStatus().equals(String.valueOf(APPROVED)) ||
                booking.getStatus().equals(REJECTED.name())) {
            throw new ValidationException("Статус бронирования уже был изменен владельцем вещи");
        }

        if (booking.getItem().getUser().getId() == userId) {
            if (approved.equals(true)) {
                booking.setStatus(String.valueOf(APPROVED));
                return bookingMapper.convertBookingToBookingDto(bookingRepository.save(booking));
            }
            if (approved.equals(false)) {
                booking.setStatus(String.valueOf(REJECTED));
                return bookingMapper.convertBookingToBookingDto(bookingRepository.save(booking));
            }
        } else if (booking.getBooker().getId() == userId) {
            if (approved.equals(false)) {
                booking.setStatus(CANCELED.name());
                return bookingMapper.convertBookingToBookingDto(bookingRepository.save(booking));
            } else {
                throw new NotFoundException("У пользователя с id = " + userId + " нет прав для подтверждения бронирования");
            }
        } else {
            throw new ValidationException("Произошла ошибка при обновлении статуса бронирования");
        }
        return null;
    }

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {

        Booking booking = (bookingRepository.findBookingByIdAndByUserId(bookingId, userId).orElseThrow(
                () -> new NotFoundException("Бронирование с id = " + bookingId + " для пользователя с id = " + userId + " не найдено")));

        return bookingMapper.convertBookingToBookingDto(booking);

    }

    @Override
    public List<BookingDto> getAllBookingsByUserIdAndStatus(long userId, String status) {
        List<String> requestStatuses = getRequestStatuses();
        if (!requestStatuses.contains(status)) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        List<BookingDto> bookings = new ArrayList<>();
        bookings = strategies.stream().filter(findBookingStrategy -> findBookingStrategy.shouldBeRun(status))
                .map(strategy -> strategy.find(userId)).flatMap(Collection::stream)
                .map(bookingMapper::convertBookingToBookingDto).collect(Collectors.toList());

        returnTrowIsEmptyList(bookings);
        return bookings;
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerAndStatus(long ownerId, String status) {
        List<String> requestStatuses = getRequestStatuses();

        if (!requestStatuses.contains(status)) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        List<BookingDto> bookings = new ArrayList<>();
        bookings = strategiesForOwner.stream().filter(findBookingByOwnerStrategy -> findBookingByOwnerStrategy.shouldBeRun(status))
                .map(strategy -> strategy.find(ownerId)).flatMap(Collection::stream)
                .map(bookingMapper::convertBookingToBookingDto).collect(Collectors.toList());

        returnTrowIsEmptyList(bookings);
        return bookings;
    }

    private User getUserOrTrow(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    private Item getItemOrTrow(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
    }

    private Booking getBookingOrTrow(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирования с id = " + bookingId + " нет в базе"));
    }

    private void compareBookingStartEnd(BookingDtoCreate bookingDtoCreate) {

        if (bookingDtoCreate.getStart().isAfter(bookingDtoCreate.getEnd())
                || bookingDtoCreate.getStart().equals(bookingDtoCreate.getEnd())) {
            throw new ValidationException("Дата окончания бронирования должна быть позже даты начала бронирования");
        }
    }

    private void returnTrowIsEmptyList(List<BookingDto> list) {

        if (list.isEmpty()) {
            throw new NotFoundException("Не найдено ни одного подходящего бронирования");
        }
    }


    private List<String> getRequestStatuses() {
        List<String> requestStatuses = new ArrayList<>();
        requestStatuses.add("FUTURE");
        requestStatuses.add("ALL");
        requestStatuses.add("WAITING");
        requestStatuses.add("REJECTED");
        requestStatuses.add("CURRENT");
        requestStatuses.add("PAST");
        return requestStatuses;
    }

}
