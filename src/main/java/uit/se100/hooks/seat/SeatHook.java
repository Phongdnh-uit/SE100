package uit.se100.hooks.seat;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uit.se100.dtos.seat.SeatRequest;
import uit.se100.dtos.seat.SeatResponse;
import uit.se100.entities.seat.Seat;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.hooks.GenericHook;
import uit.se100.repositories.flight.FlightRepository;

@RequiredArgsConstructor
@Component
public class SeatHook implements GenericHook<Seat, Long, SeatRequest, SeatResponse> {
  private final FlightRepository flightRepository;

  @Override
  public void enrichCreate(SeatRequest input, Seat entity, Map<String, Object> context) {
    enrich(input, entity);
  }

  @Override
  public void enrichUpdate(SeatRequest input, Seat entity, Map<String, Object> context) {
    enrich(input, entity);
  }

  private void enrich(SeatRequest request, Seat entity) {
    var flight =
        flightRepository
            .findById(request.getFlightId())
            .orElseThrow(
                () ->
                    new ApiException(
                        ErrorCode.RESOURCE_EXISTS,
                        "Flight not found with id: " + request.getFlightId()));
    entity.setFlight(flight);
  }
}
