package uit.se100.controllers.flight;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uit.se100.controllers.GenericController;
import uit.se100.dtos.flight.FlightRequest;
import uit.se100.dtos.flight.FlightResponse;
import uit.se100.entities.flight.Flight;
import uit.se100.services.CrudService;

@Tag(name = "Flight")
@RequestMapping("/flights")
@RestController
public class FlightController
    extends GenericController<Flight, Long, FlightRequest, FlightResponse> {
  public FlightController(CrudService<Flight, Long, FlightRequest, FlightResponse> service) {
    super(service);
  }
}
