package ru.practicum.shareit.booking.dto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Future;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import java.time.LocalDateTime;
import lombok.*;

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
