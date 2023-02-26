package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
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

    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}
