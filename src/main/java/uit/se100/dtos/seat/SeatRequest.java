package uit.se100.dtos.seat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uit.se100.enums.seat.SeatClass;

@Getter
@Setter
public class SeatRequest {
  @NotNull private Long aircraftId;

  @NotBlank private String seatNumber;

  @NotNull private SeatClass seatClass;
}
