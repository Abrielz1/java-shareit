package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import ru.practicum.shareit.user.Create;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private long id;

    @NotBlank(groups = Create.class)
    private String name;

    @NotBlank(groups = Create.class)
    private String description;

    @NotNull(groups = Create.class)
    private Boolean available;

    private Long requestId;
}