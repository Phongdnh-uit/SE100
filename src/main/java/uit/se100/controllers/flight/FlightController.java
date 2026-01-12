package uit.se100.controllers.flight;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uit.se100.controllers.GenericController;
import uit.se100.dtos.ApiResponse;
import uit.se100.dtos.PageResponse;
import uit.se100.dtos.flight.AssignSeatRequest;
import uit.se100.dtos.flight.FlightRequest;
import uit.se100.dtos.flight.FlightResponse;
import uit.se100.entities.flight.Flight;
import uit.se100.entities.flight.FlightSeat;
import uit.se100.enums.seat.SeatClass;
import uit.se100.services.CrudService;
import uit.se100.services.flight.FlightService;

@Tag(name = "Flight")
@RequestMapping("/flights")
@RestController
public class FlightController
        extends GenericController<Flight, Long, FlightRequest, FlightResponse> {
    private final FlightService flightService;

    public FlightController(
            CrudService<Flight, Long, FlightRequest, FlightResponse> service,
            FlightService flightService) {
        super(service);
        this.flightService = flightService;
    }

    @PatchMapping("/{id}/delay")
    public ResponseEntity<ApiResponse<Void>> delayFlight(
            @PathVariable("id") Long id, @RequestParam("minutes") Long minutes) {
        flightService.delayFlight(id, minutes);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelFlight(@PathVariable("id") Long id) {
        flightService.cancelFlight(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/seat/{seat-class}/available")
    public ApiResponse<PageResponse<FlightSeat>> getSeatAvailable(
            @PathVariable Long id,
            @PathVariable("seat-class") SeatClass seatClass,
            Pageable pageable

    ) {
        return ApiResponse.ok(flightService.getSeatAvailable(id, seatClass, pageable));
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/seat")
    public ApiResponse<FlightResponse> assignSeat(
            @PathVariable Long id,
            @RequestBody AssignSeatRequest request
    ) {
        return ApiResponse.ok(flightService.assignSeat(id, request));
    }
}
