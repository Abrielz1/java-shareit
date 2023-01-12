package ru.practicum.shareit.item.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class Item {
    @PositiveOrZero
    private int itemId;
    @NotBlank(message = "Некорректное название вещи")
    @Size(max = 60, message = "Слишком длинное название вещи")
    private String itemName;
    @NotNull(message = "Отсутствует описание вещи")
    @Size(min = 1, max = 200, message = "Описание превышает максимальный размер(200символов)")
    private String itemDescription;

    private Boolean isAvailable;
    @NotBlank
    @Size(max = 60, message = "Слишком длинное имя владельца")
    private String itemOwner;
    @NotBlank
    private Item itemRequest;

}
