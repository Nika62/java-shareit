package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.HelperCreationEntities.getBooking;
import static ru.practicum.shareit.HelperCreationEntities.getItem;
import static ru.practicum.shareit.HelperCreationEntities.getItemDtoForCreate;
import static ru.practicum.shareit.HelperCreationEntities.getUser;
import static ru.practicum.shareit.HelperCreationEntities.getUser2;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentServiceImplTest {
    @Autowired
    private CommentService commentService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;
    private CommentDto commentDto;

    @BeforeEach
    public void before() {
        User owner = userRepository.save(getUser());
        User booker = userRepository.save(getUser2());
        Item item = itemRepository.save(getItem());
        bookingRepository.save(new Booking(LocalDateTime.of(2023, 9, 9, 10, 17, 17), LocalDateTime.of(2023, 9, 9, 16, 17, 17), item, booker, APPROVED.name()));
    }

    @Test
    void createComment() {
        CommentDto commentDto = commentService.createComment(2, 1, new CommentDto("text comment 1", "user name2"));
        assertEquals(commentDto.getId(), 1);
        assertEquals(commentDto.getText(), "text comment 1");
        assertEquals(commentDto.getAuthorName(), "user name2");
        assertNotNull(commentDto.getCreated());

    }

    @Test
    public void shouldReturnExceptionUserNotBookerCreateComment() {
        Exception e = Assertions.assertThrows(ValidationException.class,
                () -> {
                    commentService.createComment(1, 1, new CommentDto("text comment 1", "user name2"));
                });
        assertEquals("Пользователь с id 1 не может оставить комментарий вещи с id 1", e.getMessage());
    }

    @Test
    public void shouldReturnExceptionNotFountCreateComment() {
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    commentService.createComment(99, 1, new CommentDto("text comment 1", "user name2"));
                });
        assertEquals("Пользователь c id = 99 не найден", e.getMessage());
    }

    @Test
    public void shouldReturnExceptionFountCreateComment() {
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    commentService.createComment(2, 99, new CommentDto("text comment 1", "user name2"));
                });
        assertEquals("Вещь с id 99 не найдена ", e.getMessage());
    }

    @Test
    public void getAllCommentsByItemId() {
        CommentDto commentDto = commentService.createComment(2, 1, new CommentDto("text comment 1", "user name2"));
        CommentDto commentDto2 = commentService.createComment(2, 1, new CommentDto("text comment 2", "user name2"));
        CommentDto commentDto3 = commentService.createComment(2, 1, new CommentDto("text comment 3", "user name2"));
        List<CommentDto> comments = commentService.getAllCommentsByItemId(1);
        assertEquals(comments.size(), 3);
        assertEquals(comments.get(0).getText(), "text comment 1");
        assertEquals(comments.get(1).getText(), "text comment 2");
        assertEquals(comments.get(2).getText(), "text comment 3");
    }

    @Test
    public void getAllCommentsByItemIdEmpty() {
        assertEquals(commentService.getAllCommentsByItemId(99).size(), 0);
    }
}