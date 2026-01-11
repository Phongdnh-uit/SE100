package uit.se100.hooks.flight;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uit.se100.dtos.PageResponse;
import uit.se100.dtos.flight.FlightRequest;
import uit.se100.dtos.flight.FlightResponse;
import uit.se100.dtos.flight.PriceSeatClassDto;
import uit.se100.entities.flight.Flight;
import uit.se100.entities.flight.FlightSeat;
import uit.se100.entities.seat.Seat;
import uit.se100.enums.seat.SeatClass;
import uit.se100.enums.seat.SeatStatus;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.hooks.GenericHook;
import uit.se100.projections.SeatAvailableProjection;
import uit.se100.repositories.aircraft.AircraftRepository;
import uit.se100.repositories.flight.FlightSeatRepository;
import uit.se100.repositories.route.RouteRepository;
import uit.se100.repositories.seat.SeatRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    context.put("priceSeatClass", input.getPriceSeatClass());
  }

  @Override
  public void enrichUpdate(FlightRequest input, Flight entity, Map<String, Object> context) {
    enrich(input, entity);
    context.put("priceSeatClass", input.getPriceSeatClass());
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
    // calculate duration
    if (input.getArrivalTime().isBefore(input.getDepartureTime())) {
      throw new ApiException(
          ErrorCode.VALIDATION_ERROR, "Arrival time must be after departure time");
    }
    long duration =
        input.getArrivalTime().getEpochSecond() - input.getDepartureTime().getEpochSecond();
    entity.setDurationMinutes(duration / 60);
  }

    @Override
    public void enrichUpdate(FlightRequest input, Flight entity, Map<String, Object> context) {
        enrich(input, entity);
        context.put("priceSeatClass", input.getPriceSeatClass());
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
        // calculate duration
        if (input.getArrivalTime() != null && input.getDepartureTime() != null) {
            long duration =
                    input.getArrivalTime().getEpochSecond() - input.getDepartureTime().getEpochSecond();
            entity.setDurationMinutes(duration / 60);
        }
    }

    // create flight seat from flight
    @Override
    public void afterCreate(Flight entity, FlightResponse response, Map<String, Object> context) {

        List<PriceSeatClassDto> priceSeatClassDtos =
                (List<PriceSeatClassDto>) context.getOrDefault("priceSeatClass", new ArrayList<>());

        Map<SeatClass, BigDecimal> priceMap =
                priceSeatClassDtos.stream()
                        .collect(
                                Collectors.toMap(PriceSeatClassDto::getSeatClass, PriceSeatClassDto::getPrice));

        List<Seat> seats =
                seatRepository.findAll(
                        (root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(
                                        root.get("aircraft").get("id"), response.getAircraft().getId()));

        seats.forEach(
                seat -> {
                    flightSeatRepository.save(this.createFlightSeat(entity, seat, priceMap));
                });
    }

    private FlightSeat createFlightSeat(
            Flight flight, Seat seat, Map<SeatClass, BigDecimal> priceMap) {

        SeatClass seatClass = seat.getSeatClass();

        BigDecimal price = priceMap.get(seatClass);
        if (price == null) {
            throw new ApiException(
                    ErrorCode.RESOURCE_NOT_FOUND, "Missing price for seat class: " + seatClass);
        }

        FlightSeat flightSeat = new FlightSeat();
        flightSeat.setFlight(flight);
        flightSeat.setSeat(seat);
        flightSeat.setSeatClass(seatClass);
        flightSeat.setPrice(price);
        flightSeat.setStatus(SeatStatus.AVAILABLE);

        return flightSeat;
    }

    @Override
    public void enrichFindAll(PageResponse<FlightResponse> response) {
        //add info for available flight seat number
        var result = response.getContent().stream().map(item -> {
            enrichFindById(item);

            return item;
        }).toList();

        response.setContent(result);
    }

    @Override
    public void enrichFindById(FlightResponse response) {
        List<SeatAvailableProjection> seatSummary = flightSeatRepository.countSeatsByClass(response.getId());

        response.setSeatSummary(seatSummary);
    }
}
