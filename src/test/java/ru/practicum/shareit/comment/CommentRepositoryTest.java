package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.HelperCreationEntities.*;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;


@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void getAllByItemId() {
        LocalDateTime time = LocalDateTime.now();
        userRepository.save(getUser());
        User booker = userRepository.save(getUser2());
        Item item = itemRepository.save(getItem());
        bookingRepository.save(new Booking(time, time.plusHours(1), item, booker, APPROVED.name()));
        Comment comment = commentRepository.save(new Comment("comment text", item, booker, time.plusHours(2)));
        assertEquals(comment.getId(), 1);
        assertEquals(comment.getText(), "comment text");
        assertEquals(comment.getAuthor(), booker);
        assertEquals(comment.getItem(), item);
        assertEquals(comment.getCreated(), time.plusHours(2));
    }
}