package ru.practicum.shareit.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    @PositiveOrZero
    private int itemRequestId;
    @NotBlank
    private String description;
    private LocalDateTime created;
}
