package uit.se100.services.ticket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.entities.authentication.User;
import uit.se100.entities.flight.Flight;
import uit.se100.entities.passenger.Passenger;
import uit.se100.entities.ticket.Ticket;
import uit.se100.enums.ticket.TicketStatus;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.repositories.ticket.TicketRepository;
import uit.se100.services.general.MailService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketEmailService {
    private final MailService mailService;
    private final TicketRepository ticketRepository;

    @Transactional(readOnly = true)
    public void sendPaymentSuccessEmail(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Ticket not found"));

        if (ticket.getStatus() != TicketStatus.PAID) {
            log.warn("Ticket {} is not PAID, skip sending email", ticketId);
            return;
        }

        Passenger passenger = ticket.getPassenger();
        User user = passenger.getUser(); // giả sử Passenger có @OneToOne với User

        if (user == null || user.getEmail() == null) {
            log.warn("No user/email found for ticket {}", ticketId);
            return;
        }

        Flight flight = ticket.getFlight();

        // Chuẩn bị dữ liệu cho template
        Map<String, Object> params = new HashMap<>();
        params.put("username", user.getUsername());
        params.put("ticketCode", ticket.getId());
//        params.put("seatNumber", ticket.getSeat());
        params.put("passengerName", ticket.getPassenger().getFullName());
        params.put("flightNumber", flight.getId());
        params.put("departure", flight.getDepartureTime());
        params.put("arrival", flight.getArrivalTime());
        params.put("from", flight.getRoute().getOrigin());
        params.put("to", flight.getRoute().getDestination());
        params.put("class", ticket.getTicketClass().name());
        params.put("price", ticket.getPrice().toString());
        params.put("paidAt", ticket.getPaidAt());

        String subject = String.format(
                "Xác nhận thanh toán vé thành công - Mã vé: %s",
                ticket.getId()
        );

        mailService.sendEmailFromTemplate(
                user.getEmail(),
                subject,
                "ticket-payment-success",
                params
        );

        log.info("Payment success email sent to: {} for ticket: {}", user.getEmail(), ticket.getId());
    }
}
