package uit.se100.controllers.seat;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uit.se100.controllers.GenericController;
import uit.se100.dtos.ApiResponse;
import uit.se100.dtos.seat.BatchCreateSeatRequest;
import uit.se100.dtos.seat.SeatRequest;
import uit.se100.dtos.seat.SeatResponse;
import uit.se100.entities.seat.Seat;
import uit.se100.services.CrudService;
import uit.se100.services.seat.SeatService;

@Tag(name = "Seat")
@RequestMapping("/seats")
@RestController
public class SeatController extends GenericController<Seat, Long, SeatRequest, SeatResponse> {

  private final SeatService seatService;

  public SeatController(
      CrudService<Seat, Long, SeatRequest, SeatResponse> service, SeatService seatService) {
    super(service);
    this.seatService = seatService;
  }

  @PostMapping("/bulk")
  public ResponseEntity<ApiResponse<List<SeatResponse>>> createSeatsInBulk(
      @RequestBody BatchCreateSeatRequest request) {
    return ResponseEntity.ok(ApiResponse.ok(seatService.batchCreate(request)));
  }
}
