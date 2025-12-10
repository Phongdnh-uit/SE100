package uit.se100.mappers.aircraft;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uit.se100.dtos.aircraft.AircraftRequest;
import uit.se100.dtos.aircraft.AircraftResponse;
import uit.se100.entities.aircraft.Aircraft;
import uit.se100.mappers.GenericMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AircraftMapper
    extends GenericMapper<Aircraft, AircraftRequest, AircraftResponse> {}
