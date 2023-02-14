package ru.practicum.shareit.user.dto;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    private UserDto user1Dto;

    @BeforeEach
    void beforeEach() {
        User user1 = new User(1L, "User1 name", "user1@mail.com");
        user1Dto = UserMapper.toUserDto(user1);
    }

    @Test
    void testSerialize() throws Exception {
        JsonContent<UserDto> result = json.write(user1Dto);

       Integer value = Math.toIntExact(user1Dto.getId()); //по другому ни как, не хотело в int

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathNumberValue(
                "$.id").isEqualTo(value);
        assertThat(result).extractingJsonPathStringValue(
                "$.name").isEqualTo(user1Dto.getName());
        assertThat(result).extractingJsonPathStringValue(
                "$.email").isEqualTo(user1Dto.getEmail());
    }
}