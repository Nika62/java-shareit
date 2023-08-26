package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingLastNextDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    public BookingDto convertBookingToBookingDto(Booking booking) {

        if (Objects.isNull(booking)) {
            return null;
        }

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(itemMapper.convertItemToItemDto(booking.getItem()));
        bookingDto.setBooker(userMapper.convertUserToUserDto(booking.getBooker()));
        bookingDto.setStatus(BookingStatus.valueOf(booking.getStatus()));

        return bookingDto;
    }

    public Booking convertBookingDtoToBooking(User user, Item item, BookingDto bookingDto) {
        if (Objects.isNull(bookingDto)) {
            return null;
        }

        Booking booking = new Booking();

        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Objects.nonNull(bookingDto.getStatus()) ? String.valueOf(bookingDto.getStatus()) :
                String.valueOf(WAITING));

        return booking;
    }

    public Booking convertBookingDtoCreateToBooking(User user, Item item, BookingDtoCreate bookingDtoCreate) {

        if (Objects.isNull(bookingDtoCreate)) {
            return null;
        }

        Booking booking = new Booking();

        booking.setStart(bookingDtoCreate.getStart());
        booking.setEnd(bookingDtoCreate.getEnd());
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(String.valueOf(bookingDtoCreate.getStatus()));

        return booking;
    }

    public BookingLastNextDto convertBookingToBookingLastNextDto(Booking booking) {
        if (Objects.isNull(booking)) {
            return null;
        }
        BookingLastNextDto bookingDto = new BookingLastNextDto();
        bookingDto.setId(booking.getId());
        bookingDto.setBookerId(booking.getBooker().getId());
        return bookingDto;
    }
}
