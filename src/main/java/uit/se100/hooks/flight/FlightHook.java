package uit.se100.hooks.flight;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uit.se100.dtos.flight.FlightRequest;
import uit.se100.dtos.flight.FlightResponse;
import uit.se100.entities.flight.Flight;
import uit.se100.entities.flight.FlightSeat;
import uit.se100.entities.seat.Seat;
import uit.se100.enums.seat.SeatClass;
import uit.se100.enums.seat.SeatStatus;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.hooks.GenericHook;
import uit.se100.repositories.aircraft.AircraftRepository;
import uit.se100.repositories.flight.FlightSeatRepository;
import uit.se100.repositories.route.RouteRepository;
import uit.se100.repositories.seat.SeatRepository;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class FlightHook implements GenericHook<Flight, Long, FlightRequest, FlightResponse> {
    private final RouteRepository routeRepository;
    private final AircraftRepository aircraftRepository;
    private final FlightSeatRepository flightSeatRepository;
    private final SeatRepository seatRepository;

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

    //create flight seat from flight
    @Override
    public void afterCreate(Flight entity, FlightResponse response, Map<String, Object> context) {
        List<Seat> seats = seatRepository.findAll((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("aircraft").get("id"), response.getAircraft().getId()));

        seats.forEach(seat -> {
            flightSeatRepository.save(this.createFlightSeat(entity, seat));
        });
    }

    private FlightSeat createFlightSeat(Flight flight, Seat seat) {

        SeatClass seatClass = seat.getSeatClass();

        FlightSeat flightSeat = new FlightSeat();
        flightSeat.setFlight(flight);
        flightSeat.setSeat(seat);
//        flightSeat.setPrice(seat);
        flightSeat.setSeatClass(seatClass);
        flightSeat.setStatus(SeatStatus.AVAILABLE);

        return flightSeat;
    }
}
