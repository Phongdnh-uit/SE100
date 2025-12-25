package uit.se100.entities.flight;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.entities.aircraft.Aircraft;
import uit.se100.entities.route.Route;
import uit.se100.entities.schedule.Schedule;
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

  @OneToOne(mappedBy = "flight")
  private Schedule schedule;
}
