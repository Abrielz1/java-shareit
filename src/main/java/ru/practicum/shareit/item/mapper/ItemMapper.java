package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        Long requestId = null;
        if (item.getItemRequest() != null) requestId = item.getItemRequest().getId();
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                requestId);
    }

    public static Item toItem(ItemDto itemDto, User user, ItemRequest itemRequest) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemRequest);
    }

    public static ItemDtoBooking toItemDtoBooking(Item item) {
        return new ItemDtoBooking(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                new ArrayList<>()
        );
    }
}
