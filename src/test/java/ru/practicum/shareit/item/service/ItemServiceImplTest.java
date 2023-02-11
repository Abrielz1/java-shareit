package ru.practicum.shareit.item.service;

import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.booking.storage.BookingRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import org.springframework.data.domain.PageRequest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import org.mockito.junit.jupiter.MockitoSettings;
import static org.junit.jupiter.api.Assertions.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import static org.mockito.ArgumentMatchers.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.when;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import org.mockito.InjectMocks;
import java.util.Optional;
import org.mockito.Mock;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private User user1;

    private User user2;

    private Item item1;

    private Booking booking1;

    private Comment comment1;

    private LocalDateTime now;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        user1 = new User(1L, "User1 name", "user1@mail.com");
        userRepository.save(user1);
        user2 = new User(2L, "User2 name", "user2@mail.com");
        userRepository.save(user2);
        item1 = new Item(1L, "Item1 name", "Item1 description", true, user1, null);

        booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();

        comment1 = Comment.builder()
                .id(1L)
                .text("Comment1 text")
                .item(item1)
                .author(user2)
                .created(now)
                .build();
    }

    @Test
    void findAll() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(repository.findAllByOwnerIdOrderByIdAsc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(item1));


        List<ItemDtoBooking> itemDtoBooking = itemService.findAll(user1.getId(), 0, 10);

          assertEquals(1, itemDtoBooking.size());
          assertEquals(1, itemDtoBooking.get(0).getId());
           assertEquals("Item1 name", itemDtoBooking.get(0).getName());
           assertEquals("Item1 description", itemDtoBooking.get(0).getDescription());
    }

    @Test
    void findItem() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        ItemDtoBooking itemDtoBooking = itemService.findItem(
                item1.getId(),
                user1.getId());

        assertEquals(1, itemDtoBooking.getId());
        assertEquals("Item1 name", itemDtoBooking.getName());
        assertEquals("Item1 description", itemDtoBooking.getDescription());
        assertEquals(true, itemDtoBooking.getAvailable());
    }

    @Test
    void create() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(repository.save(any(Item.class)))
                .thenReturn(item1);

        ItemDto itemDto = itemService.create(item1.getId(), ItemMapper.toItemDto(item1));

        assertEquals(1, itemDto.getId());
        assertEquals("Item1 name", itemDto.getName());
        assertEquals("Item1 description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void update() {

        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(repository.save(any(Item.class)))
                .thenReturn(item1);

        ItemDto itemDto = itemService.update(item1.getId(), user1.getId(), ItemMapper.toItemDto(item1));

        assertEquals(1, itemDto.getId());
        assertEquals("Item1 name", itemDto.getName());
        assertEquals("Item1 description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void searchItemWithNameInUpperFirstLetter() {
        when(repository.searchByText(anyString(), any(PageRequest.class)))
                .thenReturn(List.of(item1));

        List<ItemDto> itemDtos = itemService.searchItem("Item1", 0, 20);

        assertEquals(1, itemDtos.size());
        assertEquals(1, itemDtos.get(0).getId());
        assertEquals("Item1 name", itemDtos.get(0).getName());
        assertEquals("Item1 description", itemDtos.get(0).getDescription());
        assertEquals(true, itemDtos.get(0).getAvailable());
        assertNull(itemDtos.get(0).getRequestId());
    }

    @Test
    void searchItemWithNameInRandomUpperCase() {
        when(repository.searchByText(anyString(), any(PageRequest.class)))
                .thenReturn(List.of(item1));

        List<ItemDto> itemDtos = itemService.searchItem("iTem1", 0, 20);

        assertEquals(1, itemDtos.size());
        assertEquals(1, itemDtos.get(0).getId());
        assertEquals("Item1 name", itemDtos.get(0).getName());
        assertEquals("Item1 description", itemDtos.get(0).getDescription());
        assertEquals(true, itemDtos.get(0).getAvailable());
        assertNull(itemDtos.get(0).getRequestId());
    }

    @Test
    void searchItemWithDescriptionInRandomUpperCase() {
        when(repository.searchByText(anyString(), any(PageRequest.class)))
                .thenReturn(List.of(item1));

        List<ItemDto> itemDtos = itemService.searchItem("desCription", 0, 20);

        assertEquals(1, itemDtos.size());
        assertEquals(1, itemDtos.get(0).getId());
        assertEquals("Item1 name", itemDtos.get(0).getName());
        assertEquals("Item1 description", itemDtos.get(0).getDescription());
        assertEquals(true, itemDtos.get(0).getAvailable());
        assertNull(itemDtos.get(0).getRequestId());
    }

    @Test
    void searchItemWithDescriptionInUpperFirstLetter() {
        when(repository.searchByText(anyString(), any(PageRequest.class)))
                .thenReturn(List.of(item1));

        List<ItemDto> itemDtos = itemService.searchItem("desCription", 0, 20);

        assertEquals(1, itemDtos.size());
        assertEquals(1, itemDtos.get(0).getId());
        assertEquals("Item1 name", itemDtos.get(0).getName());
        assertEquals("Item1 description", itemDtos.get(0).getDescription());
        assertEquals(true, itemDtos.get(0).getAvailable());
        assertNull(itemDtos.get(0).getRequestId());
    }

    @Test
    void addComment() {
        when(bookingRepository.findByBookerIdAndItemIdAndEndBefore(
                anyLong(),
                anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(Optional.ofNullable(booking1));

        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment1);

        CommentDto commentDto = itemService
                .addComment(1L, 1L, CommentMapper.toCommentDto(comment1));

        assertEquals(1, commentDto.getId());
        assertEquals("Comment1 text", commentDto.getText());
        assertEquals("User1 name", commentDto.getAuthorName());
    }
}