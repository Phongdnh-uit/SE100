package uit.se100.hooks.seat;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uit.se100.dtos.seat.SeatRequest;
import uit.se100.dtos.seat.SeatResponse;
import uit.se100.entities.aircraft.Aircraft;
import uit.se100.entities.seat.Seat;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.hooks.GenericHook;
import uit.se100.repositories.aircraft.AircraftRepository;

@RequiredArgsConstructor
@Component
public class SeatHook implements GenericHook<Seat, Long, SeatRequest, SeatResponse> {
  private final AircraftRepository aircraftRepository;

  @Override
  public void validateCreate(SeatRequest input, Map<String, Object> context) {
    validate(input, context);
  }

  @Override
  public void validateUpdate(
      Long id, SeatRequest input, Seat existingEntity, Map<String, Object> context) {
    validate(input, context);
  }

  @Override
  public void enrichCreate(SeatRequest input, Seat entity, Map<String, Object> context) {
    enrich(input, entity, context);
  }

  @Override
  public void enrichUpdate(SeatRequest input, Seat entity, Map<String, Object> context) {
    enrich(input, entity, context);
  }

  private void validate(SeatRequest request, Map<String, Object> context) {
    var aircraft =
        aircraftRepository
            .findById(request.getAircraftId())
            .orElseThrow(() -> new ApiException(ErrorCode.VALIDATION_ERROR));
    context.put("aircraft", aircraft);
  }

  private void enrich(SeatRequest request, Seat entity, Map<String, Object> context) {
    var aircraft = (Aircraft) context.get("aircraft");
    entity.setAircraft(aircraft);
  }
}
