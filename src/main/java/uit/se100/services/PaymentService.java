package uit.se100.services;

import uit.se100.dtos.payment.PayTicketRequest;
import uit.se100.dtos.payment.PaymentResponse;
import uit.se100.entities.payment.Transaction;

/**
 * PaymentService Interface
 * <p>
 * Định nghĩa các phương thức để xử lý thanh toán cho vé máy bay
 * Hỗ trợ nhiều phương thức thanh toán thông qua Strategy Pattern
 */
public interface PaymentService {

    /**
     * Tạo thanh toán cho một vé với chi tiết đầy đủ
     *
     * @param ticketId       ID của vé
     * @param paymentRequest Thông tin chi tiết thanh toán
     * @return PaymentResponse chứa URL thanh toán và thông tin giao dịch
     */
    PaymentResponse createPaymentForTicket(Long ticketId, PayTicketRequest paymentRequest);

    Transaction refundTransaction(Long transactionId);
}
