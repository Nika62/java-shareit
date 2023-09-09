package shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.util.List;


@Service
public interface BookingService {

    BookingDto createBooking(long userId, BookingDtoCreate bookingDtoCreate);

    BookingDto updateBooking(long bookingId, Boolean approved, long userId);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getAllBookingsByUserIdAndStatus(long userId, String status, int from, int size);

    List<BookingDto> getAllBookingsByOwnerAndStatus(long ownerId, String status, int from, int size);

}
