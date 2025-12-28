package uit.se100.entities.ticket;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.entities.flight.Flight;
import uit.se100.entities.passenger.Passenger;
import uit.se100.entities.seat.Seat;
import uit.se100.enums.seat.SeatClass;
import uit.se100.enums.ticket.TicketStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "ticket")
public class Ticket extends BaseEntity {

    // ===== Relationships =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    // ===== Business fields =====

    @Enumerated(EnumType.STRING)
    @Column(name = "class", nullable = false)
    private SeatClass ticketClass;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    private Instant bookedAt;

    private Instant paidAt;
}
