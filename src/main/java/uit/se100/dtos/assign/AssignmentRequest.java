package uit.se100.dtos.assign;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentRequest {
  @NotNull private Long flightId;

  @NotEmpty private List<Long> employeeIds;
}
