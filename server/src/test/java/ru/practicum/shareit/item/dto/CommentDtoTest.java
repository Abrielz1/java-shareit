package ru.practicum.shareit.item.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    private CommentDto comment1Dto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        User user1 = new User(1L, "User1 name", "user1@mail.com");
        User user2 = new User(2L, "User2 name", "user2@mail.com");
        Item item1 = new Item(0L, "Item1 name", "Item1 description", true, user1, null);

        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Comment1 text")
                .item(item1)
                .author(user2)
                .created(now)
                .build();
        comment1Dto = CommentMapper.toCommentDto(comment1);
    }

    @Test
    void testSerialize() throws Exception {
        JsonContent<CommentDto> result = json.write(comment1Dto);
        Integer commentId1 = Math.toIntExact(comment1Dto.getId());

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(commentId1);
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo(comment1Dto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(comment1Dto.getAuthorName());
    }
}