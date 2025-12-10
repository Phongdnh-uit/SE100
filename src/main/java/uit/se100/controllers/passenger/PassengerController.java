package uit.se100.controllers.passenger;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uit.se100.controllers.GenericController;
import uit.se100.dtos.passenger.PassengerRequest;
import uit.se100.dtos.passenger.PassengerResponse;
import uit.se100.entities.passenger.Passenger;
import uit.se100.services.CrudService;

@Tag(name = "Passenger")
@RequestMapping("/passengers")
@RestController
public class PassengerController extends GenericController<Passenger, Long, PassengerRequest, PassengerResponse> {

    public PassengerController(CrudService<Passenger, Long, PassengerRequest, PassengerResponse> service) {
        super(service);
    }
}
