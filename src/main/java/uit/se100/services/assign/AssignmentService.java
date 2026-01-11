package uit.se100.services.assign;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import uit.se100.dtos.PageResponse;
import uit.se100.dtos.assign.AssignmentRequest;
import uit.se100.dtos.assign.CrewAssignmentResponse;
import uit.se100.entities.assign.CrewAssignment;

public interface AssignmentService {
  void assignEmployeesToFlight(AssignmentRequest request);

  PageResponse<CrewAssignmentResponse> findAll(
      Pageable pageable, Specification<CrewAssignment> spec);

  PageResponse<CrewAssignmentResponse> findAllByEmployeeId(
      Long employeeId, Pageable pageable, Specification<CrewAssignment> spec);
}
