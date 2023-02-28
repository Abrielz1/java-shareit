package ru.practicum.shareit.request.storage;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import static org.junit.jupiter.api.Assertions.*;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;

    private ItemRequest itemRequest1;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        user1 = new User(1L, "User1 name", "user1@mail.com");
        user1 = userRepository.save(user1);

        itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("ItemRequest1 description")
                .requestor(user1)
                .created(now)
                .build();
        itemRequest1 = itemRequestRepository.save(itemRequest1);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findAllByRequesterId() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(user1.getId());

        assertEquals(List.of(itemRequest1), itemRequests);
    }

    @Test
    void findAllPageable() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(user1.getId());

        assertEquals(List.of(itemRequest1), itemRequests);
    }
}