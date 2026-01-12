package uit.se100.entities.flight;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.entities.seat.Seat;
import uit.se100.enums.seat.SeatClass;
import uit.se100.enums.seat.SeatStatus;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "flight_seats")
public class FlightSeat extends BaseEntity {
    @ManyToOne()
    @JoinColumn(name = "flight_id")
    private Flight flight;

    @ManyToOne()
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @Enumerated(EnumType.STRING)
    private SeatClass seatClass;

    private BigDecimal price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatStatus status;
}
