package uit.se100.dtos.flight;

import lombok.Getter;
import lombok.Setter;
import uit.se100.enums.seat.SeatClass;

import java.math.BigDecimal;

@Getter
@Setter
public class PriceSeatClassDto {
    private SeatClass seatClass;
    private BigDecimal price;
}
