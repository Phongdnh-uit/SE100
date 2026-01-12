package uit.se100.services.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.dtos.payment.PayTicketRequest;
import uit.se100.dtos.payment.PaymentRequest;
import uit.se100.dtos.payment.PaymentResponse;
import uit.se100.entities.payment.Transaction;
import uit.se100.entities.ticket.Ticket;
import uit.se100.enums.payments.PaymentMethod;
import uit.se100.enums.payments.TransactionStatus;
import uit.se100.enums.payments.TransactionType;
import uit.se100.repositories.payment.TransactionRepository;
import uit.se100.repositories.ticket.TicketRepository;
import uit.se100.services.PaymentService;
import uit.se100.services.payment.strategy.PaymentStrategy;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * PaymentService Implementation
 * <p>
 * Sử dụng Strategy Pattern để hỗ trợ nhiều phương thức thanh toán
 * (MoMo, VNPay, Bank Transfer, Credit Card, etc.)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final TransactionRepository transactionRepository;
    private final TicketRepository ticketRepository;
    // Inject tất cả payment strategies
    private final Map<PaymentMethod, PaymentStrategy> paymentStrategies;

    /**
     * Tạo thanh toán cho một vé
     *
     * @param ticketId ID của vé cần thanh toán
     * @return PaymentResponse chứa thông tin thanh toán
     */
    @Override
    @Transactional
    public PaymentResponse createPaymentForTicket(Long ticketId, PayTicketRequest payTicketRequest) {
        log.info("Creating payment for ticket: {}", ticketId);

        // Lấy thông tin vé
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

        // Tạo transaction entity
        Transaction transaction = createTransaction(ticket);

        try {
            // Lấy payment strategy tương ứng
            PaymentStrategy strategy = getPaymentStrategy(payTicketRequest.getPaymentMethod());

            PaymentRequest paymentRequest = PaymentRequest.builder()
                    .amount(ticket.getPrice())
                    .ticketId(ticketId)
                    .build();

            // Thực hiện thanh toán thông qua strategy
            PaymentResponse response = strategy.processPayment(paymentRequest, transaction);

            // Lưu transaction
            transactionRepository.save(transaction);

            log.info("Payment created successfully for ticket: {}", ticketId);
            return response;

        } catch (Exception e) {
            log.error("Error creating payment for ticket: {}", ticketId, e);
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailReason(e.getMessage());
            transactionRepository.save(transaction);

            return PaymentResponse.builder()
                    .transactionId(transaction.getId())
                    .ticketId(ticketId)
                    .status(TransactionStatus.FAILED)
                    .message("Payment creation failed: " + e.getMessage())
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
    }

    /**
     * Xác nhận thanh toán từ callback của payment provider
     *
     * @param paymentMethod Phương thức thanh toán
     * @param callbackData  Dữ liệu callback từ provider
     * @return Transaction đã được cập nhật
     */
    @Transactional
    public Transaction verifyPaymentCallback(PaymentMethod paymentMethod, String callbackData) {
        log.info("Verifying payment callback for method: {}", paymentMethod);

        try {
            PaymentStrategy strategy = getPaymentStrategy(paymentMethod);
            Transaction transaction = strategy.verifyPaymentCallback(callbackData);

            if (transaction != null) {
                transaction.setPaidAt(Instant.now());
                transactionRepository.save(transaction);
                log.info("Payment verified successfully");
            }

            return transaction;

        } catch (Exception e) {
            log.error("Error verifying payment callback", e);
            throw new RuntimeException("Payment verification failed: " + e.getMessage());
        }
    }

    /**
     * Hoàn tiền một giao dịch
     *
     * @param transactionId ID giao dịch cần hoàn tiền
     * @return Transaction refund mới
     */
    @Transactional
    public Transaction refundTransaction(Long transactionId) {
        log.info("Refunding transaction: {}", transactionId);

        Transaction originalTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));

        try {
            PaymentStrategy strategy = getPaymentStrategy(
                    PaymentMethod.fromCode(originalTransaction.getType().name())
            );

            Transaction refundTransaction = strategy.refundTransaction(
                    transactionId
            );

            if (refundTransaction != null) {
                refundTransaction.setType(TransactionType.REFUND);
                refundTransaction.setPaidAt(Instant.now());
                transactionRepository.save(refundTransaction);
                log.info("Refund processed successfully");
            }

            return refundTransaction;

        } catch (Exception e) {
            log.error("Error refunding transaction: {}", transactionId, e);
            throw new RuntimeException("Refund failed: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra trạng thái giao dịch
     *
     * @param transactionId ID giao dịch
     * @return Trạng thái giao dịch
     */
    public String checkTransactionStatus(Long transactionId) {
        log.info("Checking transaction status: {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));

        try {
            PaymentStrategy strategy = getPaymentStrategy(
                    PaymentMethod.fromCode(transaction.getType().name())
            );

            return strategy.checkTransactionStatus(transaction.getProviderTxnRef());

        } catch (Exception e) {
            log.error("Error checking transaction status: {}", transactionId, e);
            throw new RuntimeException("Status check failed: " + e.getMessage());
        }
    }

    /**
     * Lấy payment strategy dựa trên payment method
     *
     * @param paymentMethod Phương thức thanh toán
     * @return PaymentStrategy tương ứng
     */
    private PaymentStrategy getPaymentStrategy(PaymentMethod paymentMethod) {
        PaymentStrategy strategy = paymentStrategies.get(paymentMethod);
        if (strategy == null) {
            throw new RuntimeException("Payment strategy not found for method: " + paymentMethod);
        }
        return strategy;
    }

    /**
     * Tạo transaction entity
     *
     * @param ticket Vé cần thanh toán
     * @return Transaction entity mới
     */
    private Transaction createTransaction(Ticket ticket) {
        Transaction transaction = new Transaction();
        transaction.setTicket(ticket);
        transaction.setType(TransactionType.TICKET_PAYMENT);
        transaction.setAmount(ticket.getPrice());
        transaction.setStatus(TransactionStatus.PENDING);

        // Tạo provider transaction reference (unique ID do hệ thống tạo)
        transaction.setProviderTxnRef(generateProviderTxnRef());

        return transaction;
    }

    /**
     * Tạo provider transaction reference
     *
     * @return Unique reference ID
     */
    private String generateProviderTxnRef() {
        // Format: TXN_TIMESTAMP_UUID
        return "TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}

