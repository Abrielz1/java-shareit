package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoBooking;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {

    List<ItemDtoBooking> findAll(long userId, Pageable page);

    ItemDtoBooking findItem(long userId, long itemId);

    List<ItemDto> searchItem(String text, Pageable page);

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
