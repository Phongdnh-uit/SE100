package uit.se100.controllers.flight;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uit.se100.controllers.GenericController;
import uit.se100.dtos.ApiResponse;
import uit.se100.dtos.flight.FlightRequest;
import uit.se100.dtos.flight.FlightResponse;
import uit.se100.entities.flight.Flight;
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
}
