package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class User {
    @PositiveOrZero
    private long userId;
    @NotBlank
    private String userName;
    @NotBlank(message = "Отсутствует email")
    @Email(message = "Некорректный email")
    @Size(max = 50)
    private String userEmail;
}
