package ru.practicum.shareit.user.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {
    private Long id;
    @NotBlank(groups = Create.class)
    private String name;
    @NotNull(groups = Create.class)
    @Email(groups = {Create.class, Update.class})
    private String email;
}