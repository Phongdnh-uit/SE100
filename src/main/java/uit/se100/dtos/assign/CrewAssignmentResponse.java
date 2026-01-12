package uit.se100.dtos.assign;

import lombok.Getter;
import lombok.Setter;
import uit.se100.dtos.employee.EmployeeResponse;
import uit.se100.dtos.flight.FlightResponse;

@Getter
@Setter
public class CrewAssignmentResponse {
  private Long id;
  private FlightResponse flight;
  private EmployeeResponse employee;
}
