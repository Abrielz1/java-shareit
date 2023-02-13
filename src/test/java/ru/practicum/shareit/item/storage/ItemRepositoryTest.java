package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user1;

    private User user2;

    private ItemRequest itemRequest1;

    private Item item1;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        user1 = new User(1L, "User1 name", "user1@mail.com");
        userRepository.save(user1);
        user2 = new User(2L, "User2 name", "user2@mail.com");
        userRepository.save(user2);

        itemRequest1 = new ItemRequest(1L, user1, "ItemRequest1 description", now);
        itemRequestRepository.save(itemRequest1);

        item1 = new Item(1L, "Item1 name", "Item1 description", true, user1, itemRequest1);
        itemRepository.save(item1);
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findAllByOwnerIdOrderByIdAscTest() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(user1.getId(), PageRequest.of(0, 10));
        List<Item> items1 = new ArrayList<>();
        items1.add(item1);

        assertEquals(items1.get(0).getId(), items.get(0).getId());
        assertEquals(items1.get(0).getName(), items.get(0).getName());
        assertEquals(items1.get(0).getDescription(), items.get(0).getDescription());
    }

    @Test
    void searchByTextTestFindDescription() {

        String text = "description";
        List<Item> items = itemRepository.searchByText(text, PageRequest.of(0, 10));

        assertEquals(List.of(item1).size(), items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertEquals(item1.getName(), items.get(0).getName());
    }

    @Test
    void searchByTextTestFindName() {
        String text = "name";

        List<Item> items = itemRepository.searchByText(text, PageRequest.of(0, 10));

        assertEquals(List.of(item1).size(), items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertEquals(item1.getName(), items.get(0).getName());
    }

    @Test
    void searchByRequestsIdTest() {
        List<Long> ids = itemRepository.findAll().stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.searchByRequestsId(ids);

        assertEquals(List.of(item1).size(), items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertEquals(item1.getName(), items.get(0).getName());
    }
}