package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

@Service
public interface ItemService {

    ItemDto create(long owner, ItemDto itemDto);

    ItemDto update(long owner, long itemId, ItemDto itemDto);

    List<ItemDto> findAll(long owner);

    ItemDto findItem(long itemId);

    List<ItemDto> searchItem(String text);
}
