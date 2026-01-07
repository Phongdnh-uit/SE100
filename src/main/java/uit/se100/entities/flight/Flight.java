package uit.se100.entities.flight;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.entities.aircraft.Aircraft;
import uit.se100.entities.route.Route;
import uit.se100.enums.flight.FlightStatus;

@Getter
@Setter
@Entity
@Table(name = "flights")
public class Flight extends BaseEntity {
  @ManyToOne
  @JoinColumn(name = "route_id", nullable = false)
  private Route route;

  @ManyToOne
  @JoinColumn(name = "aircraft_id", nullable = false)
  private Aircraft aircraft;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private FlightStatus status;

  @Column(nullable = false)
  private Instant departureTime;

  @Column(nullable = false)
  private Instant arrivalTime;

  private Long durationMinutes;
}
