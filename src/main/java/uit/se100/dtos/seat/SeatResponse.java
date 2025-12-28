package uit.se100.dtos.seat;

import lombok.Getter;
import lombok.Setter;
import uit.se100.dtos.aircraft.AircraftResponse;
import uit.se100.entities.BaseEntity;
import uit.se100.enums.seat.SeatClass;

@Getter
@Setter
public class SeatResponse extends BaseEntity {
  private AircraftResponse aircraft;

  private String seatNumber;

  private SeatClass seatClass;
}
