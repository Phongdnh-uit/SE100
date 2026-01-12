package uit.se100.dtos.statistics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFlightHoursDTO {
    private Long employeeId;
    private String fullName;
    private String position;
    private Integer totalFlightHours;
    private Integer maxFlightHoursPerMonth;
    private Long totalFlightsAssigned;
}
