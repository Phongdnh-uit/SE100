package uit.se100.dtos.statistics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengersPerFlightDTO {
    private Long flightId;
    private String origin;
    private String destination;
    private Instant departureTime;
    private String flightStatus;
    private Long passengerCount;
}
