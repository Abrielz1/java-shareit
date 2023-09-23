package ru.practicum.shareit.item.dto;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    private ItemDto item1Dto;

    @BeforeEach
    void beforeEach() {
        User user1 = new User(1L, "User1 name", "user1@mail.com");

        Item item = new Item(0L, "Item1 name", "Item1 description", true, user1, null);
        item1Dto = ItemMapper.toItemDto(item);
    }

    @Test
    void testSerialize() throws Exception {
        JsonContent<ItemDto> result = json.write(item1Dto);
        Integer itemId = Math.toIntExact(item1Dto.getId());

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemId);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(item1Dto.getName());
        assertThat(result).extractingJsonPathStringValue(
                "$.description").isEqualTo(item1Dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(item1Dto.getAvailable());
    }
}