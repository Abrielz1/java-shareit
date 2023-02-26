package ru.practicum.shareit.booking.dto;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingStatus;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    private BookingDto booking1Dto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        User user1 = new User(1L, "User1 name", "user1@mail.com");
        User user2 = new User(2L, "User2 name", "user2@mail.com");

        Item item1 = Item.builder()
                .id(1L)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .itemRequest(null)
                .build();

        Booking booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        booking1Dto = BookingMapper.toBookingDto(booking1);
    }

    @Test
    void testSerialize() throws Exception {
        JsonContent<BookingDto> result = json.write(booking1Dto);
        Integer id = Math.toIntExact(booking1Dto.getId());
        Integer itemId = Math.toIntExact(booking1Dto.getItemId());
        Integer bookerId = Math.toIntExact(booking1Dto.getBookerId());

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).hasJsonPath("$.status");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(id);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(itemId);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(bookerId);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(booking1Dto.getStatus().toString());
    }
}