package uit.se100.dtos.seat;

import lombok.Getter;
import lombok.Setter;
import uit.se100.dtos.flight.FlightResponse;
import uit.se100.entities.BaseEntity;
import uit.se100.enums.seat.SeatClass;
import uit.se100.enums.seat.SeatStatus;

@Getter
@Setter
public class SeatResponse extends BaseEntity {
  private FlightResponse flight;

  private String seatNumber;

  private SeatClass seatClass;

  private SeatStatus status;
}
