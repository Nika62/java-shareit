package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerializeUserDto() throws Exception {
        UserDto userDto = new UserDto(
                1L,
                "имя",
                "почта.doe@mail.com"
        );
        JsonContent<UserDto> result = json.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("имя");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("почта.doe@mail.com");
    }

    @Test
    void testDeserializeUserDto() throws Exception {
        String content = "{\"id\":1,\"name\":\"имя\",\"email\":\"почта.doe@mail.com\"}";

        assertThat(this.json.parse(content)).isEqualTo(new UserDto(1L, "имя", "почта.doe@mail.com"));
        assertThat(this.json.parseObject(content).getName()).isEqualTo("имя");
    }

}