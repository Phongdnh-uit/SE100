package uit.se100.hooks.flight;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uit.se100.dtos.flight.FlightRequest;
import uit.se100.dtos.flight.FlightResponse;
import uit.se100.entities.flight.Flight;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.hooks.GenericHook;
import uit.se100.repositories.aircraft.AircraftRepository;
import uit.se100.repositories.route.RouteRepository;

@RequiredArgsConstructor
@Component
public class FlightHook implements GenericHook<Flight, Long, FlightRequest, FlightResponse> {
  private final RouteRepository routeRepository;
  private final AircraftRepository aircraftRepository;

  @Override
  public void enrichCreate(FlightRequest input, Flight entity, Map<String, Object> context) {
    enrich(input, entity);
  }

  @Override
  public void enrichUpdate(FlightRequest input, Flight entity, Map<String, Object> context) {
    enrich(input, entity);
  }

  private void enrich(FlightRequest input, Flight entity) {
    var route =
        routeRepository
            .findById(input.getRouteId())
            .orElseThrow(
                () ->
                    new ApiException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Route with id " + input.getRouteId() + " not found"));
    var aircraft =
        aircraftRepository
            .findById(input.getAircraftId())
            .orElseThrow(
                () ->
                    new ApiException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Aircraft with id " + input.getAircraftId() + " not found"));
    entity.setRoute(route);
    entity.setAircraft(aircraft);
  }
}
