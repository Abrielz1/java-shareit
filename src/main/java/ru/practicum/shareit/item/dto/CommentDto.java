package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import ru.practicum.shareit.user.Create;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;

    @NotNull(groups = Create.class)
    @NotBlank(groups = Create.class)
    @Size(min = 5, max = 100)
    private String text;

    private String authorName;

    private LocalDateTime created;
}
