package uit.se100.controllers.assign;

import io.github.perplexhub.rsql.RSQLJPASupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uit.se100.dtos.ApiResponse;
import uit.se100.dtos.PageResponse;
import uit.se100.dtos.assign.AssignmentRequest;
import uit.se100.dtos.assign.CrewAssignmentResponse;
import uit.se100.entities.assign.CrewAssignment;
import uit.se100.services.assign.AssignmentService;

@Tag(name = "Assignment")
@RequestMapping("/assignments")
@RequiredArgsConstructor
@RestController
public class AssignmentController {
  private final AssignmentService assignmentService;

  @PostMapping("/assign")
  public ResponseEntity<ApiResponse<Void>> assingEmployeesToFlight(
      @Valid @RequestBody AssignmentRequest request) {
    assignmentService.assignEmployeesToFlight(request);
    return ResponseEntity.ok(ApiResponse.ok(null));
  }

  @GetMapping("/all")
  public ResponseEntity<ApiResponse<PageResponse<CrewAssignmentResponse>>> getAllAssignments(
      @ParameterObject Pageable pageable,
      @RequestParam(value = "filter", required = false) String filter,
      @RequestParam(value = "all", required = false, defaultValue = "false") boolean all) {
    if (all) {
      pageable = Pageable.unpaged(pageable.getSort());
    }
    Specification<CrewAssignment> spec = RSQLJPASupport.toSpecification(filter);
    return ResponseEntity.ok(ApiResponse.ok(assignmentService.findAll(pageable, spec)));
  }

  @GetMapping("/all/employee/{employeeId}")
  public ResponseEntity<ApiResponse<PageResponse<CrewAssignmentResponse>>>
      getAllAssignmentsByEmployeeId(
          @PathVariable("employeeId") Long employeeId,
          @ParameterObject Pageable pageable,
          @RequestParam(value = "filter", required = false) String filter,
          @RequestParam(value = "all", required = false, defaultValue = "false") boolean all) {
    if (all) {
      pageable = Pageable.unpaged(pageable.getSort());
    }
    Specification<CrewAssignment> spec = RSQLJPASupport.toSpecification(filter);
    return ResponseEntity.ok(
        ApiResponse.ok(assignmentService.findAllByEmployeeId(employeeId, pageable, spec)));
  }
}
