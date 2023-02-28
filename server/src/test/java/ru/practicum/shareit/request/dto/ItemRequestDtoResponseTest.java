package ru.practicum.shareit.request.dto;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.model.ItemRequest;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

@JsonTest
class ItemRequestDtoResponseTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private ItemRequestDto itemRequest1Dto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        User user1 = new User(1L, "User1 name", "user1@mail.com");

        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("ItemRequest1 description")
                .requestor(user1)
                .created(now)
                .build();
        itemRequest1Dto = ItemRequestMapper.toItemRequestDto(itemRequest1);

        Item item1 = Item.builder()
                .id(1L)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .itemRequest(itemRequest1)
                .build();
        ItemDto item1Dto = ItemMapper.toItemDto(item1);
    }

    @Test
    void testSerialize() throws Exception {
        JsonContent<ItemRequestDto> result = json.write(itemRequest1Dto);

        Integer id = Math.toIntExact(itemRequest1Dto.getId());
        Integer userid = Math.toIntExact(itemRequest1Dto.getRequestorid());

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.requestorid");
        assertThat(result).hasJsonPath("$.created");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(id);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequest1Dto.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.requestorid").isEqualTo(userid);
    }
}