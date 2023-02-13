package ru.practicum.shareit.booking.dto;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import static org.assertj.core.api.Assertions.assertThat;
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
class BookingDtoResponseTest {

    @Autowired
    private JacksonTester<BookingDtoResponse> json;

    private BookingDtoResponse booking1DtoResponse;

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
        booking1DtoResponse = BookingMapper.toBookingDtoResponse(booking1);
    }

    @Test
    void testSerialize() throws Exception {
        JsonContent<BookingDtoResponse> result = json.write(booking1DtoResponse);

        Integer id = Math.toIntExact(booking1DtoResponse.getId());
        Integer itemId = Math.toIntExact(booking1DtoResponse.getItem().getId());
        Integer bookerId = Math.toIntExact(booking1DtoResponse.getBooker().getId());

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(id);
      assertThat(result).extractingJsonPathNumberValue("$.item.id")
              .isEqualTo(itemId);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(bookerId);
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(booking1DtoResponse.getStatus().toString());
    }
}