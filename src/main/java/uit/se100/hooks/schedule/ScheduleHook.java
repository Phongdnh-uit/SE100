package uit.se100.hooks.schedule;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uit.se100.dtos.schedule.ScheduleRequest;
import uit.se100.dtos.schedule.ScheduleResponse;
import uit.se100.entities.flight.Flight;
import uit.se100.entities.schedule.Schedule;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.hooks.GenericHook;
import uit.se100.repositories.flight.FlightRepository;

@RequiredArgsConstructor
@Component
public class ScheduleHook
    implements GenericHook<Schedule, Long, ScheduleRequest, ScheduleResponse> {

  private final FlightRepository flightRepository;

  @Override
  public void validateCreate(ScheduleRequest input, Map<String, Object> context) {
    Flight flight =
        flightRepository
            .findById(input.getFlightId())
            .orElseThrow(
                () ->
                    new ApiException(
                        ErrorCode.RESOURCE_NOT_FOUND, Map.of("flightId", "Flight not found")));
    // Add to context for later use
    context.put("flight", flight);
  }

  @Override
  public void enrichCreate(ScheduleRequest input, Schedule entity, Map<String, Object> context) {
    Flight flight = (Flight) context.get("flight");
    entity.setFlight(flight);
  }
}
