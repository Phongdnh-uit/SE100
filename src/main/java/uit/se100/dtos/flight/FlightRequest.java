package uit.se100.dtos.flight;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import uit.se100.enums.flight.FlightStatus;

@Getter
@Setter
public class FlightRequest {
  @NotNull private Long routeId;

  @NotNull private Long aircraftId;

  @NotNull private FlightStatus status;

  @NotNull private List<PriceSeatClassDto> priceSeatClass;

  @NotNull private Instant departureTime;

  @NotNull private Instant arrivalTime;
}
