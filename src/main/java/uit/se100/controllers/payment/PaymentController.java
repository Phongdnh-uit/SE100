package uit.se100.controllers.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import uit.se100.entities.payment.Transaction;
import uit.se100.entities.ticket.Ticket;
import uit.se100.enums.payments.PaymentMethod;
import uit.se100.enums.payments.TransactionStatus;
import uit.se100.services.payment.PaymentServiceImpl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PaymentController
 * <p>
 * REST API endpoints để xử lý thanh toán
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Getter
@Setter
public class PaymentController {

    private final PaymentServiceImpl paymentService;
    @Value("${app.client.base-url}")
    private String clientUrl;

    /**
     * Callback từ VNPay
     * <p>
     * VNPay sẽ redirect người dùng đến URL này sau khi thanh toán
     * <p>
     * GET /api/v1/payments/vnpay-callback?vnp_TxnRef=...&vnp_Amount=...&vnp_ResponseCode=...&vnp_SecureHash=...
     */
    @GetMapping("/vnpay-callback")
    public RedirectView vnpayCallback(
            @RequestParam Map<String, String> allParams) {
        String callbackData = allParams.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        log.info("VNPay callback received - txnRef: {}, responseCode: {}", allParams.get("txnRef"), allParams.get("responseCode"));

        try {
            Transaction transaction = paymentService.verifyPaymentCallback(PaymentMethod.VNPAY, callbackData);


            if ("00".equals(allParams.get("vnp_ResponseCode"))) {
                if (transaction != null && transaction.getStatus() == TransactionStatus.SUCCESS) {
                    Ticket ticket = transaction.getTicket(); // Giả sử Transaction có @ManyToOne Ticket
                    String ticketCode = URLEncoder.encode(String.valueOf(ticket.getId()), StandardCharsets.UTF_8);
                    String url = clientUrl + "/payment/success" + "?ticketId=" + ticket.getId() +
                            "&ticketCode=" + ticketCode +
                            "&status=success";

                    return new RedirectView(url);
                }
            } else {
                String failReason = URLEncoder.encode(
                        transaction != null ? transaction.getFailReason() : "Thanh toán thất bại",
                        StandardCharsets.UTF_8
                );
                String url = clientUrl + "/payment/failed?reason=" + failReason;

                return new RedirectView(url);
            }

        } catch (Exception e) {
            log.error("VNPay callback processing failed", e);
            String errorMsg = URLEncoder.encode("Lỗi xử lý callback: " + e.getMessage(), StandardCharsets.UTF_8);
            String url = clientUrl + "/payment/failed?message=" + errorMsg;
            return new RedirectView(url);
        }
        return new RedirectView();
    }

//    /**
//     * Callback từ ZaloPay
//     * <p>
//     * ZaloPay sẽ POST dữ liệu callback tới URL này sau khi thanh toán
//     * <p>
//     * POST /api/v1/payments/zalopay-callback
//     * Content-Type: application/json
//     * {
//     * "return_code": 1,
//     * "return_message": "success",
//     * "sub_return_code": 1,
//     * "sub_return_message": "success",
//     * "zp_trans_id": 123456789,
//     * "server_time": 1234567890,
//     * "amount": 500000,
//     * "discount_amount": 0,
//     * "item": "[]",
//     * "zp_request_id": "request_123",
//     * "mac": "..."
//     * }
//     */
//    @PostMapping("/zalopay-callback")
//    public ResponseEntity<String> zaloPayCallback(
//            @RequestBody String callbackData) {
//
//        log.info("ZaloPay callback received");
//
//        try {
//            // Verify callback signature and update transaction
//            Transaction transaction = paymentService.verifyPaymentCallback(PaymentMethod.ZALOPAY, callbackData);
//
//            if (transaction != null) {
//                log.info("ZaloPay payment verified successfully");
//                return ResponseEntity.ok("Success");
//            } else {
//                log.warn("ZaloPay payment verification failed");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed");
//            }
//
//        } catch (Exception e) {
//            log.error("Error processing ZaloPay callback", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Callback processing failed");
//        }
//    }

    /**
     * Hoàn tiền cho một giao dịch
     * <p>
     * POST /api/v1/payments/{transactionId}/refund
     */
    @PostMapping("/{transactionId}/refund")
    public ResponseEntity<String> refundTransaction(
            @PathVariable Long transactionId) {

        log.info("Refunding transaction: {}", transactionId);

        try {
            Transaction refundTransaction = paymentService.refundTransaction(transactionId);

            if (refundTransaction != null) {
                return ResponseEntity.ok("Refund processed successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refund processing failed");
            }

        } catch (Exception e) {
            log.error("Error refunding transaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Refund failed");
        }
    }

    /**
     * Kiểm tra trạng thái giao dịch
     * <p>
     * GET /api/v1/payments/{transactionId}/status
     */
    @GetMapping("/{transactionId}/status")
    public ResponseEntity<String> checkTransactionStatus(
            @PathVariable Long transactionId) {

        log.info("Checking transaction status: {}", transactionId);

        try {
            String status = paymentService.checkTransactionStatus(transactionId);
            return ResponseEntity.ok(status);

        } catch (Exception e) {
            log.error("Error checking transaction status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Status check failed");
        }
    }

    /**
     * Lấy danh sách phương thức thanh toán có sẵn
     * <p>
     * GET /api/v1/payments/methods
     */
    @GetMapping("/methods")
    public ResponseEntity<PaymentMethod[]> getAvailablePaymentMethods() {
        return ResponseEntity.ok(PaymentMethod.values());
    }
}

