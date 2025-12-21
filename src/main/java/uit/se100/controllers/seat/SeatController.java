package uit.se100.controllers.seat;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uit.se100.controllers.GenericController;
import uit.se100.dtos.seat.SeatRequest;
import uit.se100.dtos.seat.SeatResponse;
import uit.se100.entities.seat.Seat;
import uit.se100.services.CrudService;

@Tag(name = "Seat")
@RequestMapping("/seats")
@RestController
public class SeatController extends GenericController<Seat, Long, SeatRequest, SeatResponse> {

  public SeatController(CrudService<Seat, Long, SeatRequest, SeatResponse> service) {
    super(service);
  }
}
