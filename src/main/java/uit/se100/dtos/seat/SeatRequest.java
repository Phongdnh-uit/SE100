package uit.se100.dtos.seat;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uit.se100.enums.seat.SeatClass;
import uit.se100.enums.seat.SeatStatus;

@Getter
@Setter
public class SeatRequest {
  @NotNull private Long flightId;

  @NotNull private String seatNumber;

  @NotNull private SeatClass seatClass;

  @NotNull private SeatStatus status;
}
