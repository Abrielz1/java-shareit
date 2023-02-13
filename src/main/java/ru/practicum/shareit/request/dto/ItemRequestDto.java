package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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

    @NotBlank(groups = Create.class)
    @Size(groups = Create.class, min = 1, max = 200)
    private String description;

    private LocalDateTime created;
}
