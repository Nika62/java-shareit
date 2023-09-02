package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.ResponseState;
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

        if (booking.getStatus().equals(APPROVED.name()) ||
                booking.getStatus().equals(REJECTED.name())) {
            throw new ValidationException("Статус бронирования уже был изменен владельцем вещи");
        }
        return updateBookingStatus(item.getUser().getId(), userId, approved, booking);
    }

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {

        Booking booking = (bookingRepository.findBookingByIdAndByUserId(bookingId, userId).orElseThrow(
                () -> new NotFoundException("Бронирование с id = " + bookingId + " для пользователя с id = " + userId + " не найдено")));

        return bookingMapper.convertBookingToBookingDto(booking);

    }

    @Override
    public List<BookingDto> getAllBookingsByUserIdAndStatus(long userId, String status, int from, int size) {
        if (!EnumUtils.isValidEnum(ResponseState.class, status)) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);

        Page<Booking> bookings = strategies.stream().filter(findBookingStrategy -> findBookingStrategy.shouldBeRun(status))
                .map(strategy -> strategy.find(userId, pageRequest)).collect(Collectors.toList()).get(0);
        List<BookingDto> bookingsDto = bookings.get().map(bookingMapper::convertBookingToBookingDto).collect(Collectors.toList());

        returnTrowIsEmptyList(bookingsDto);
        return bookingsDto;
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerAndStatus(long ownerId, String status, int from, int size) {
        if (!EnumUtils.isValidEnum(ResponseState.class, status)) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);

        Page<Booking> bookings = strategiesForOwner.stream().filter(findBookingByOwnerStrategy -> findBookingByOwnerStrategy.shouldBeRun(status))
                .map(strategy -> strategy.find(ownerId, pageRequest)).collect(Collectors.toList()).get(0);

        List<BookingDto> bookingsDto = bookings.get().map(bookingMapper::convertBookingToBookingDto).collect(Collectors.toList());

        returnTrowIsEmptyList(bookingsDto);
        return bookingsDto;
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

    private BookingDto updateBookingStatus(long bookingItemUserId, long responseUserId, Boolean approved, Booking booking) {

        if (bookingItemUserId == responseUserId) {
            if (approved.equals(true)) {
                return setBookingStatusApproved(booking);
            }
            if (approved.equals(false)) {
                return setBookingStatusReject(booking);
            }
        } else if (bookingItemUserId != responseUserId) {
            if (approved.equals(false)) {
                return setBookingStatusCanceled(booking);
            } else {
                throw new NotFoundException("У пользователя с id = " + responseUserId + " нет прав для подтверждения бронирования");
            }
        }
        throw new ValidationException("Произошла ошибка при обновлении статуса бронирования");
    }

    private BookingDto setBookingStatusApproved(Booking booking) {
        booking.setStatus(String.valueOf(APPROVED));
        return bookingMapper.convertBookingToBookingDto(bookingRepository.save(booking));
    }

    private BookingDto setBookingStatusReject(Booking booking) {
        booking.setStatus(String.valueOf(REJECTED));
        return bookingMapper.convertBookingToBookingDto(bookingRepository.save(booking));
    }

    private BookingDto setBookingStatusCanceled(Booking booking) {
        booking.setStatus(CANCELED.name());
        return bookingMapper.convertBookingToBookingDto(bookingRepository.save(booking));
    }
}

