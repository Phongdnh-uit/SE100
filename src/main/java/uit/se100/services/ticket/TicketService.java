package uit.se100.services.ticket;

import org.springframework.data.domain.Pageable;
import uit.se100.dtos.PageResponse;
import uit.se100.dtos.ticket.ReserveTicketRequest;
import uit.se100.dtos.ticket.TicketResponse;

public interface TicketService {

    TicketResponse reserveTicket(ReserveTicketRequest request);

    PageResponse<TicketResponse> findByPassengerId(Pageable pageable);
    //
//    void payTicket(Long ticketId);
//
//    void cancelTicket(Long ticketId);

//    SeatSummaryResponse getSeatSummary(Long flightId);
}
