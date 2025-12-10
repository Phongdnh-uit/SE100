package uit.se100.controllers.aircraft;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uit.se100.controllers.GenericController;
import uit.se100.dtos.aircraft.AircraftRequest;
import uit.se100.dtos.aircraft.AircraftResponse;
import uit.se100.entities.aircraft.Aircraft;
import uit.se100.services.CrudService;

@RequestMapping("/aircrafts")
@RestController
public class AircraftController
    extends GenericController<Aircraft, Long, AircraftRequest, AircraftResponse> {

  public AircraftController(
      CrudService<Aircraft, Long, AircraftRequest, AircraftResponse> service) {
    super(service);
  }
}
