package uit.se100.entities.schedule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.entities.flight.Flight;

@Getter
@Setter
@Entity
@Table(name = "schedules")
public class Schedule extends BaseEntity {
  @OneToOne
  @JoinColumn(name = "flight_id")
  private Flight flight;

  @Column(nullable = false)
  private Instant departureTime;

  @Column(nullable = false)
  private Instant arrivalTime;

  private Long durationMinutes;

  // Optional fields for delay information
  // private Long delayMinutes;
  // private String delayReason;
}
