package shareit.booking.finding;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;

public interface FindBookingByOwnerStrategy {

    boolean shouldBeRun(String status);

    Page<Booking> find(long userId, PageRequest pageRequest);
}
