package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingLastNextDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.practicum.shareit.HelperCreationEntities.*;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerializeItemDto() throws Exception {
        UserDto userDto = getUserDto();
        CommentDto commentDto = getCommentDto();
        List<CommentDto> comments = List.of(commentDto);
        BookingLastNextDto lastBookingDto = getLastBookingDto();
        BookingLastNextDto nextBookingDto = getNextBookingDto();

        ItemDto itemDto = getItemDto();
        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.user.name").isEqualTo("user name");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("text comment");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(3);
    }

    @Test
    void testDeserializeItemDto() throws Exception {
        String content = "{\"id\":1, " +
                "\"name\":\"item name\", " +
                "\"description\":\"item description\", " +
                "\"available\": true, " +
                " \"user\": {\"id\":1,\"name\":\"user name\",\"email\":\"user@mail.com\"}, " +
                "\"requestId\":2, " +
                "\"comments\":[{\"id\":1, \"text\":\"text comment\", \"authorName\":\"name author comment\", \"created\": \"2023-09-03T09:09:01\"}], " +
                "\"lastBooking\":{\"id\":2, \"bookerId\":3}, " +
                "\"nextBooking\":{\"id\":3, \"bookerId\":4} }";

        System.out.println(json.parse(content));
        assertThat(this.json.parse(content)).isEqualTo(getItemDto());

    }
}
