package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
public class ItemDto {

    public ItemDto(String itemName, String itemDescription, Boolean isAvailable, int req) {
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(

                item.getItemName(),
                item.getItemDescription(),
                item.getIsAvailable(),
                item.getItemRequest() != null ? item.getItemRequest().getItemId() : null
        );

    }
}
