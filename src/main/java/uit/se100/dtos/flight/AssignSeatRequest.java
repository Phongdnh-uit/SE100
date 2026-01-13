package uit.se100.dtos.flight;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignSeatRequest {
    Long ticketId;
    Long seatId;
}
