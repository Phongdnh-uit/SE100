package uit.se100.controllers.ticket;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uit.se100.dtos.PageResponse;
import uit.se100.dtos.payment.PayTicketRequest;
import uit.se100.dtos.payment.PaymentResponse;
import uit.se100.dtos.ticket.ReserveTicketRequest;
import uit.se100.dtos.ticket.TicketResponse;
import uit.se100.services.payment.PaymentServiceImpl;
import uit.se100.services.ticket.TicketService;

@RestController
@RequestMapping("/tickets")
@Tag(name = "Ticket")
@Slf4j
@RequiredArgsConstructor
public class TickerController {
    private final TicketService ticketService;
    private final PaymentServiceImpl paymentService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@RequestBody @Valid ReserveTicketRequest reserveTicketRequest) {
        return this.ticketService.reserveTicket(reserveTicketRequest);
    }

    @GetMapping("/own")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<TicketResponse> getOwnTickets(Pageable pageable) {
        return this.ticketService.findByPassengerId(pageable);
    }


    /**
     * Tạo yêu cầu thanh toán cho một vé
     * <p>
     * POST /api/v1/payments/create
     * <p>
     * Body:
     * {
     * "ticketId": 1,
     * "amount": 2500000,
     * "paymentMethod": "MOMO",
     * "description": "Ticket payment",
     * "returnUrl": "https://yourapp.com/payment-callback",
     * "cancelUrl": "https://yourapp.com/payment-cancel",
     * "ipAddress": "192.168.1.1",
     * "userAgent": "Mozilla/5.0..."
     * }
     */

    @PostMapping("/tickets/{id}/payments")
    public ResponseEntity<PaymentResponse> createPaymentForTicket(
            @RequestBody PayTicketRequest paymentRequest,
            @PathVariable Long id) {

        log.info("Creating payment for ticket: {}", id);

        try {
            PaymentResponse response = paymentService.createPaymentForTicket(
                    id,
                    paymentRequest
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error creating payment", e);

            PaymentResponse errorResponse = PaymentResponse.builder()
                    .message("Payment creation failed: " + e.getMessage())
                    .timestamp(System.currentTimeMillis())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
