package uit.se100.dtos.payment;

import lombok.*;
import uit.se100.enums.payments.PaymentMethod;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long ticketId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
}

