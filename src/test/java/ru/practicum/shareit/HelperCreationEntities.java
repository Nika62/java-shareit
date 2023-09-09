package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingLastNextDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

public class HelperCreationEntities {

    public static UserDto getUserDtoWithoutId() {
        return new UserDto("user no Id", "usernoid@mail.com");
    }

    public static UserDto getUserDto() {
        return new UserDto(1, "user name", "user@mail.com");
    }

    public static UserDto getUserDtoId3() {
        return new UserDto(3, "user name3", "user3@mail.com");
    }

    public static List<UserDto> getListUserDto() {
        return List.of(getUserDto(), new UserDto(2, "user2 mame", "user2@mail.com"));
    }

    public static User getUser() {
        return new User("user name", "user@mail.com");
    }

    public static User getUserALLField() {
        return new User(2l, "user name2", "user2@mail.com");
    }

    public static User getUser2() {
        return new User("user name2", "user2@mail.com");
    }

    public static CommentDto getCommentDto() {
        return new CommentDto(1l, "text comment", "name author comment", LocalDateTime.of(2023, 9, 3, 9, 9, 1));
    }

    public static CommentDto getCommentDto2() {
        return new CommentDto(2l, "text comment2", "name author comment2", LocalDateTime.of(2023, 9, 3, 10, 10, 1));
    }

    public static List<CommentDto> getListCommentsDto() {
        return List.of(getCommentDto(), getCommentDto2());
    }

    public static BookingLastNextDto getLastBookingDto() {
        return new BookingLastNextDto(2L, 3L);
    }

    public static BookingLastNextDto getNextBookingDto() {
        return new BookingLastNextDto(3L, 4L);
    }

    public static ItemDto getItemDtoForCreate() {
        return new ItemDto("item name create", "item description create", true);
    }

    public static ItemDto getItemDtoForCreateRequestId() {
        return new ItemDto("item reply request", "description item reply request", true, 1);
    }

    public static ItemDto getItemDto() {
        return new ItemDto(1L, "item name", "item description", true, getUserDto(), 2, List.of(getCommentDto()), getLastBookingDto(), getNextBookingDto());
    }

    public static ItemDto getItemDtoWithoutComments() {
        return new ItemDto(2L, "item name", "item description", true, getUserDto(), 2, getLastBookingDto(), getNextBookingDto());
    }

    public static List<ItemDto> getListItemDto() {
        return List.of(getItemDto(), getItemDtoWithoutComments());
    }

    public static Item getItem() {
        return new Item("item name", "item description", true, new User(1, "user name", "user@mail.com"));
    }

    public static Item getItemAllField() {
        return new Item(1l, "item name", "item description", true, new User(1, "user name", "user@mail.com"));
    }

    public static RequestDto getRequestForCreate() {
        return new RequestDto("request description", LocalDateTime.of(2023, 9, 3, 10, 10, 1));
    }

    public static BookingDto getBookingDto() {
        return new BookingDto(1, LocalDateTime.of(2023, 9, 4, 10, 10, 1), LocalDateTime.of(2023, 9, 5, 10, 10, 1), getUserDtoId3(), getItemDto(), WAITING);
    }

    public static Booking getBookingAfterCreate() {
        return new Booking(1l, LocalDateTime.of(2023, 9, 4, 10, 10, 1), LocalDateTime.of(2023, 9, 5, 10, 10, 1), getItemAllField(), getUserALLField(), WAITING.name());
    }

    public static Booking getBooking() {
        return new Booking(LocalDateTime.of(2023, 9, 4, 10, 10, 1), LocalDateTime.of(2023, 9, 5, 10, 10, 1), WAITING.name());
    }
}
