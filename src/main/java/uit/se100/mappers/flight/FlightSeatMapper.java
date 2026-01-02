package uit.se100.mappers.flight;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uit.se100.dtos.flight.FlightSeatResponse;
import uit.se100.entities.flight.FlightSeat;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface FlightSeatMapper {
    FlightSeatResponse toDto(FlightSeat flightSeat);
}
