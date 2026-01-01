package uit.se100.dtos.flight;

import uit.se100.enums.seat.SeatClass;

import java.math.BigDecimal;
import java.time.Instant;

public record FlightSeatResponse(
        Long id,
        SeatClass seatClass,
        BigDecimal price,
        Instant createdAt
) {
}
