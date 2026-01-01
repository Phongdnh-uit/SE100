package uit.se100.mappers.ticket;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uit.se100.dtos.ticket.ReserveTicketRequest;
import uit.se100.dtos.ticket.TicketResponse;
import uit.se100.entities.ticket.Ticket;
import uit.se100.mappers.flight.FlightMapper;
import uit.se100.mappers.flight.FlightSeatMapper;
import uit.se100.mappers.passenger.user.PassengerMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {FlightSeatMapper.class, FlightMapper.class, PassengerMapper.class})
public interface TicketMapper {
    Ticket toTicket(ReserveTicketRequest request);

    TicketResponse toResponse(Ticket ticket);
}
