package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String name;

    private String email;
}