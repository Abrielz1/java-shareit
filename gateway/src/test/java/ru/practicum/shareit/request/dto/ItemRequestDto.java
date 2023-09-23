package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
}