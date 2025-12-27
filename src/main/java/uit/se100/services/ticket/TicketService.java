package uit.se100.services.ticket;

import uit.se100.dtos.ticket.ReserveTicketRequest;
import uit.se100.dtos.ticket.TicketResponse;

public interface TicketService {

    TicketResponse reserveTicket(ReserveTicketRequest request);
//
//    void payTicket(Long ticketId);
//
//    void cancelTicket(Long ticketId);

//    SeatSummaryResponse getSeatSummary(Long flightId);
}
