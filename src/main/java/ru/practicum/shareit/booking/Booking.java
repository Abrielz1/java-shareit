package ru.practicum.shareit.booking;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class Booking {
    @PositiveOrZero
    private int bookingId;
    private LocalDateTime start;
    private LocalDateTime end;
    @NotBlank
    private String booker;
    private Enum status;
}
