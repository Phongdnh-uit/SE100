package uit.se100.dtos.flight;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uit.se100.enums.flight.FlightStatus;

@Getter
@Setter
public class FlightRequest {
  @NotNull private Long routeId;

  @NotNull private Long aircraftId;

  @NotNull private FlightStatus status;
}
