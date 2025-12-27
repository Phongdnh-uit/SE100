package uit.se100.dtos.ticket;

import lombok.Getter;
import lombok.Setter;
import uit.se100.dtos.flight.FlightResponse;
import uit.se100.dtos.passenger.PassengerResponse;
import uit.se100.dtos.seat.SeatResponse;
import uit.se100.enums.seat.SeatClass;
import uit.se100.enums.ticket.TicketStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class TicketResponse {
    private Long id;
    private FlightResponse flight;
    private PassengerResponse passenger;
    private SeatResponse seat;

    private TicketStatus status;
    private SeatClass ticketClass;
    private BigDecimal price;
    private Instant bookedAt;
    private Instant paidAt;
}
