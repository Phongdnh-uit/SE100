package uit.se100.dtos.baggage;

import lombok.Getter;
import lombok.Setter;
import uit.se100.dtos.flight.FlightResponse;
import uit.se100.dtos.passenger.PassengerResponse;
import uit.se100.entities.BaseEntity;
import uit.se100.enums.baggage.BaggageType;

import java.math.BigDecimal;

@Getter
@Setter
public class BaggageResponse extends BaseEntity {

    private BaggageType type;

    private BigDecimal weight;

    private BigDecimal extraFee;

    private PassengerResponse passenger;

    private FlightResponse flight;
}

