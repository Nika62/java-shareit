package shareit.booking.finding.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.finding.FindBookingByOwnerStrategy;
import ru.practicum.shareit.booking.model.Booking;

import static ru.practicum.shareit.booking.BookingStatus.REJECTED;

@Component
@RequiredArgsConstructor
public class FindByOwnerRejected implements FindBookingByOwnerStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public boolean shouldBeRun(String status) {
        return status.equals(BookingStatus.REJECTED.name());
    }

    @Override
    public Page<Booking> find(long userId, PageRequest pageRequest) {
        return bookingRepository.findAllByOwnerIdAndBookingStatus(userId, BookingStatus.REJECTED.name(), pageRequest);

    }
}
