package uit.se100.entities.seat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.entities.aircraft.Aircraft;
import uit.se100.enums.seat.SeatClass;

@Getter
@Setter
@Entity
@Table(name = "seats")
public class Seat extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatClass seatClass;

}
