package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<ItemDtoBooking> findAll(long userId) {
        log.info("Items sent");
        return itemRepository.findAllByOwnerIdOrderByIdAsc(userId).stream()
                .map(item -> setBookings(userId, item))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoBooking findItem(long userId, long itemId) {
        log.info("Item sent");
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Вещь не найдена");
        });
        return setComments(setBookings(userId, item), itemId);
    }

    @Override
    @Transactional
    public ItemDto create(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь не найден");
        });
        log.info("Item created");
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user));
        itemDto.setId(item.getId());
        return itemDto;
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Item not found for update");
        });
        if (item.getOwner().getId() == userId) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            itemRepository.save(item);
            log.info("Item updated");
        } else {
            throw new ObjectNotFoundException("Вещь для обновления не найдена");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.info("Search results sent");
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchByText(text.toLowerCase())
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
        Comment comment = CommentMapper.toComment(user, item, commentDto);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    private ItemDtoBooking setBookings(long userId, Item item) {
        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item);
        if (item.getOwner().getId() == userId) {
            itemDtoBooking.setLastBooking(
                    bookingRepository.findLastBooking(
                            itemDtoBooking.getId(), LocalDateTime.now()
                    ).map(BookingMapper::toBookingDto).orElse(null));
            itemDtoBooking.setNextBooking(
                    bookingRepository.findNextBooking(
                            itemDtoBooking.getId(), LocalDateTime.now()
                    ).map(BookingMapper::toBookingDto).orElse(null));
        } else {
            itemDtoBooking.setLastBooking(null);
            itemDtoBooking.setNextBooking(null);
        }
        return itemDtoBooking;
    }

    private ItemDtoBooking setComments(ItemDtoBooking itemDtoBooking, long itemId) {
        List<CommentDto> commentDtos = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDtoBooking.setComments(commentDtos);
        return itemDtoBooking;
    }
}
