package uit.se100.dtos.baggage;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uit.se100.enums.baggage.BaggageType;

import java.math.BigDecimal;

@Getter
@Setter
public class BaggageRequest {

    @NotNull(message = "Baggage type is required")
    private BaggageType type;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.01", message = "Weight must be greater than 0")
    private BigDecimal weight;

    @NotNull(message = "Passenger ID is required")
    private Long passengerId;

    @NotNull(message = "Flight ID is required")
    private Long flightId;
}

