package uit.se100.dtos.schedule;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import uit.se100.dtos.Action.Create;

@Getter
@Setter
public class ScheduleRequest {
  @NotNull(groups = {Create.class})
  private Long flightId;

  @NotNull private Instant departureTime;
  @NotNull private Instant arrivalTime;
}
