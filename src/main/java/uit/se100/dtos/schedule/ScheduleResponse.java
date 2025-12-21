package uit.se100.dtos.schedule;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import uit.se100.dtos.flight.FlightResponse;
import uit.se100.entities.BaseEntity;

@Getter
@Setter
public class ScheduleResponse extends BaseEntity {
  private FlightResponse flight;
  private Instant departureTime;
  private Instant arrivalTime;
  private Long durationMinutes;
}
