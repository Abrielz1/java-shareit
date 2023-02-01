package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
