package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import ru.practicum.shareit.common.Create;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CommentDto {
    private Long id;
    @NotNull(groups = Create.class)
    @NotBlank(groups = Create.class)
    @Size(min = 5, max = 100)
    private String text;
    private String authorName;
    private LocalDateTime created;
}
