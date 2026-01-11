package uit.se100.dtos.payment;

import lombok.Getter;
import lombok.Setter;
import uit.se100.enums.payments.PaymentMethod;

@Getter
@Setter
public class PayTicketRequest {
    PaymentMethod paymentMethod;
    String returnUrl;
    String cancelUrl;
}
