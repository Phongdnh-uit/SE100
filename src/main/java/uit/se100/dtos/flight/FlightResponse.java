package uit.se100.dtos.flight;

import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import uit.se100.dtos.aircraft.AircraftResponse;
import uit.se100.dtos.route.RouteResponse;
import uit.se100.dtos.schedule.ScheduleResponse;
import uit.se100.entities.BaseEntity;
import uit.se100.enums.flight.FlightStatus;

@Getter
@Setter
public class FlightResponse extends BaseEntity {
  private List<FlightSeatResponse> flightSeats;
  private RouteResponse route;
  private AircraftResponse aircraft;
  private FlightStatus status;
  private ScheduleResponse schedule;
  private Instant departureTime;
  private Instant arrivalTime;
  private Long durationMinutes;
}
