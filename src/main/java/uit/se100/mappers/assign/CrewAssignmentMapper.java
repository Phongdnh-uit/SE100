package uit.se100.mappers.assign;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uit.se100.dtos.assign.CrewAssignmentResponse;
import uit.se100.entities.assign.CrewAssignment;
import uit.se100.mappers.flight.FlightMapper;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {FlightMapper.class})
public interface CrewAssignmentMapper {
  @Mapping(target = "employeeId", source = "employee.id")
  CrewAssignmentResponse entityToResponse(CrewAssignment assignment);
}
