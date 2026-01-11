package uit.se100.services.assign;

import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uit.se100.dtos.PageResponse;
import uit.se100.dtos.assign.AssignmentRequest;
import uit.se100.dtos.assign.CrewAssignmentResponse;
import uit.se100.entities.assign.CrewAssignment;
import uit.se100.enums.employee.EmployeePosition;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.mappers.assign.CrewAssignmentMapper;
import uit.se100.repositories.assign.AssignmentRepository;
import uit.se100.repositories.employee.EmployeeRepository;
import uit.se100.repositories.flight.FlightRepository;

@RequiredArgsConstructor
@Service
public class AssignmentServiceImpl implements AssignmentService {
  private final FlightRepository flightRepository;
  private final AssignmentRepository assignmentRepository;
  private final EmployeeRepository employeeRepository;
  private final CrewAssignmentMapper crewAssignmentMapper;

  @Transactional
  @Override
  public void assignEmployeesToFlight(AssignmentRequest request) {
    // validate flight exists
    Map<String, String> errors = new HashMap<>();
    var flightOpt = flightRepository.findById(request.getFlightId());
    if (flightOpt.isEmpty()) {
      errors.put("flightId", "Flight does not exist.");
      throw new ApiException(ErrorCode.VALIDATION_ERROR, errors);
    }
    var employees =
        employeeRepository.findAll(
            (root, query, builder) ->
                builder.and(
                    root.get("id").in(request.getEmployeeIds()),
                    builder.or(
                        builder.equal(root.get("position"), EmployeePosition.PILOT),
                        builder.equal(root.get("position"), EmployeePosition.COPILOT),
                        builder.equal(root.get("position"), EmployeePosition.ATTENDANT))));
    // phi công không được bay quá 100 giờ/tháng
    // tiếp viên không được bay quá 80 giờ/tháng
    // Một phi hành đoàn phải có ít nhất 2 phi công và 4 tiếp viên cho mỗi chuyến bay nội địa và 2
    // phi công và 6 tiếp viên cho mỗi chuyến bay quốc tế
    boolean isInternational = flightOpt.get().getRoute().isExternal();
    var pilots = employees.stream().filter(e -> e.getPosition() == EmployeePosition.PILOT).toList();
    var copilots =
        employees.stream().filter(e -> e.getPosition() == EmployeePosition.COPILOT).toList();
    var attendants =
        employees.stream().filter(e -> e.getPosition() == EmployeePosition.ATTENDANT).toList();
    if (pilots.size() < 2) {
      errors.put("employeeIds", "At least 2 pilots are required.");
    }
    if (isInternational) {
      if (attendants.size() < 6) {
        errors.put("employeeIds", "At least 6 attendants are required for international flights.");
      }
    } else {
      if (attendants.size() < 4) {
        errors.put("employeeIds", "At least 4 attendants are required for domestic flights.");
      }
    }

    // Check for accumulated flight hours
    Long flightDurationInHours = flightOpt.get().getDurationMinutes() / 60;
    for (var pilot : pilots) {
      Long accumulatedHours = pilot.getTotalFlightHours() + flightDurationInHours;
      if (accumulatedHours + flightDurationInHours > pilot.getMaxFlightHoursPerMonth()) {
        errors.put(
            "employeeIds", "Pilot " + pilot.getId() + " exceeds monthly flight hours limit.");
      }
    }
    for (var attendant : attendants) {
      Long accumulatedHours = attendant.getTotalFlightHours() + flightDurationInHours;
      if (accumulatedHours + flightDurationInHours > attendant.getMaxFlightHoursPerMonth()) {
        errors.put(
            "employeeIds",
            "Attendant " + attendant.getId() + " exceeds monthly flight hours limit.");
      }
    }
    if (!errors.isEmpty()) {
      throw new ApiException(ErrorCode.VALIDATION_ERROR, errors);
    }

    // proceed to assign
    // drop existing assignments
    flightOpt.get().getCrewAssignments().clear();
    for (var employee : employees) {
      CrewAssignment assignment = new CrewAssignment();
      assignment.setFlight(flightOpt.get());
      assignment.setEmployee(employee);
      flightOpt.get().getCrewAssignments().add(assignment);
    }
    flightRepository.save(flightOpt.get());
  }

  @Override
  public PageResponse<CrewAssignmentResponse> findAll(
      Pageable pageable, Specification<CrewAssignment> spec) {
    Page<CrewAssignment> page = assignmentRepository.findAll(spec, pageable);
    return PageResponse.fromPage(page.map(crewAssignmentMapper::entityToResponse));
  }

  @Override
  public PageResponse<CrewAssignmentResponse> findAllByEmployeeId(
      Long employeeId, Pageable pageable, Specification<CrewAssignment> spec) {
    Specification<CrewAssignment> employeeSpec =
        (root, query, builder) -> builder.equal(root.get("employee").get("id"), employeeId);
    Specification<CrewAssignment> combinedSpec = spec.and(employeeSpec);
    Page<CrewAssignment> page = assignmentRepository.findAll(combinedSpec, pageable);
    return PageResponse.fromPage(page.map(crewAssignmentMapper::entityToResponse));
  }
}
