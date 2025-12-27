package uit.se100.dtos.baggage;

import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.enums.baggage.BaggageType;

import java.math.BigDecimal;

@Getter
@Setter
public class BaggageRequest extends BaseEntity {

    private Long passengerId;

    private Long flightId;

    private BaggageType type;

    private BigDecimal weight;

    private BigDecimal extraFee;
}
