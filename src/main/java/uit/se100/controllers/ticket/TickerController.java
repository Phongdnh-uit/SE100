package uit.se100.controllers.ticket;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uit.se100.dtos.ticket.ReserveTicketRequest;
import uit.se100.dtos.ticket.TicketResponse;
import uit.se100.services.ticket.TicketService;

@RestController
@RequestMapping("/tickets")
@Tag(name = "Ticket")
@RequiredArgsConstructor
public class TickerController {
    private final TicketService ticketService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@RequestBody @Valid ReserveTicketRequest reserveTicketRequest) {
        return this.ticketService.reserveTicket(reserveTicketRequest);
    }
}
