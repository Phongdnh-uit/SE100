package uit.se100.dtos.statistics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AircraftStatusStatisticsDTO {
    private Long activeCount;
    private Long maintenanceCount;
    private Long inactiveCount;
    private Long totalAircraftCount;
}
