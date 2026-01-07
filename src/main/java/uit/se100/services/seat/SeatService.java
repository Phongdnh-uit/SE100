package uit.se100.services.seat;

import java.util.List;
import uit.se100.dtos.seat.BatchCreateSeatRequest;
import uit.se100.dtos.seat.SeatResponse;

public interface SeatService {
  List<SeatResponse> batchCreate(BatchCreateSeatRequest request);
}
