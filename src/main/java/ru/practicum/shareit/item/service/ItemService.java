package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {

    List<ItemDtoBooking> findAll(long userId, int from, int size);

    ItemDtoBooking findItem(long userId, long itemId);

    List<ItemDto> searchItem(String text, int from, int size);

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
