package uit.se100.dtos.ticket;

import uit.se100.enums.seat.SeatClass;

public record ReserveTicketRequest(
        Long flightId,
        SeatClass seatClass
) {
}
