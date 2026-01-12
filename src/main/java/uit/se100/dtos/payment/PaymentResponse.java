package uit.se100.dtos.payment;

import lombok.*;
import uit.se100.enums.payments.PaymentMethod;
import uit.se100.enums.payments.TransactionStatus;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long transactionId;
    private Long ticketId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private TransactionStatus status;
    private String paymentUrl;
    private String providerTxnRef;
    private String providerTxnNo;
    private String message;
    private long timestamp;
}

