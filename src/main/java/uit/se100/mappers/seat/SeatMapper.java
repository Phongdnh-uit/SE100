package uit.se100.mappers.seat;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uit.se100.dtos.seat.SeatRequest;
import uit.se100.dtos.seat.SeatResponse;
import uit.se100.entities.seat.Seat;
import uit.se100.mappers.GenericMapper;
import uit.se100.mappers.aircraft.AircraftMapper;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {AircraftMapper.class})
public interface SeatMapper extends GenericMapper<Seat, SeatRequest, SeatResponse> {}
