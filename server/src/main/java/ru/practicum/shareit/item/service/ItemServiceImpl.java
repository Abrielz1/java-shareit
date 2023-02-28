package ru.practicum.shareit.item.service;

import ru.practicum.shareit.request.storage.ItemRequestRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Comment;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {

    private final CommentRepository commentRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final ItemRequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDto create(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь не найден");
        });
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = requestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new ObjectNotFoundException("Запрос не найден"));
        }
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user, itemRequest));
        itemDto.setId(item.getId());
        log.info("Вещь создана");
        return itemDto;
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Вещь для обновления не найдена");
        });
        if (item.getOwner().getId() == userId) {
            if (itemDto.getName() != null) item.setName(itemDto.getName());
            if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
            if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
            itemRepository.save(item);
            log.info("Вещь обновлена");
        } else {
            throw new ObjectNotFoundException("Вещь для обновления не найдена");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDtoBooking> findAll(long userId, Pageable page) {
        log.info("Вещи отправлены");
        return setAllBookingsAndComments(userId, itemRepository.findAllByOwnerIdOrderByIdAsc(userId, page));
    }


    @Override
    public ItemDtoBooking findItem(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Вещь для обновления не найдена");
        });
        log.info("Вещь найдена");
        return setAllBookingsAndComments(userId, Collections.singletonList(item)).get(0);
    }

    @Override
    public List<ItemDto> searchItem(String text, Pageable page) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        text = text.toUpperCase();
        log.info("Отправлены результаты поиска");
        return itemRepository.searchByText(text.toLowerCase(), page)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Item not found"));
        bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Вы не можете комментировать эту вещь"));
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = CommentMapper.toComment(user, item, commentDto);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    private List<ItemDtoBooking> setAllBookingsAndComments(long userId, List<Item> items) {
        List<Long> ids = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findBookingsLast(ids, LocalDateTime.now(), userId);
        Map<Long, ItemDtoBooking> itemsMap = items.stream()
                .map(ItemMapper::toItemDtoBooking)
                .collect(Collectors.toMap(ItemDtoBooking::getId, item -> item, (a, b) -> b));
        bookings.forEach(booking -> itemsMap.get(booking.getItem().getId())
                .setLastBooking(BookingMapper.toBookingDto(booking)));
        bookings = bookingRepository.findBookingsNext(ids, LocalDateTime.now(), userId);
        bookings.forEach(booking -> itemsMap.get(booking.getItem().getId())
                .setNextBooking(BookingMapper.toBookingDto(booking)));
        List<Comment> comments = commentRepository.findAllComments(ids);
        comments.forEach(comment -> itemsMap.get(comment.getItem().getId())
                .getComments().add(CommentMapper.toCommentDto(comment)));
        return new ArrayList<>(itemsMap.values());
    }
}
