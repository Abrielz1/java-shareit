package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import javax.validation.constraints.Size;
import ru.practicum.shareit.user.Create;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemRequestDtoResponse {

    private Long id;

    private Long requestorId;

    @Size(groups = Create.class, min = 1, max = 200)
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}
