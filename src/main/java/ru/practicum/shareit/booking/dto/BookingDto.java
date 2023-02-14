package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.enums.BookingStatus;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Future;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import lombok.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    @NotNull(groups = Update.class)
    private Long id;

    @FutureOrPresent(groups = Create.class)
    private LocalDateTime start;

    @Future(groups = Create.class)
    private LocalDateTime end;

    @NotNull(groups = Create.class)
    private Long itemId;

    private Long bookerId;

    private BookingStatus status;
}


