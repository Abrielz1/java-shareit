package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.Create;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;

    private Long requestorid;

    private String description;

    private LocalDateTime created;
}
