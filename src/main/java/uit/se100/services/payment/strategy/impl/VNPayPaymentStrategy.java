package uit.se100.services.payment.strategy.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import uit.se100.dtos.payment.PaymentRequest;
import uit.se100.dtos.payment.PaymentResponse;
import uit.se100.entities.payment.Transaction;
import uit.se100.enums.payments.PaymentMethod;
import uit.se100.enums.payments.TransactionStatus;
import uit.se100.services.payment.strategy.PaymentStrategy;
import uit.se100.utils.MathUtils;
import uit.se100.utils.SecurityUtils;
import uit.se100.utils.VNPayUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * VNPay Payment Strategy Implementation
 * <p>
 * Hỗ trợ thanh toán qua VNPay gateway
 * Tham khảo: https://sandbox.vnpayment.vn/
 */
@Slf4j
@Configuration
@Getter
@Setter
public class VNPayPaymentStrategy implements PaymentStrategy {
    @Value("${payment.vnPay.url}")
    private String vnp_PayUrl;
    @Value("${payment.vnPay.returnUrl}")
    private String vnp_ReturnUrl;

    @Value("${payment.vnPay.tmnCode}")
    private String vnp_TmnCode;
    @Value("${payment.vnPay.secretKey}")
    private String secretKey;

    @Value("${payment.vnPay.expiredTime}")
    private int expiredTime;

    @Override
    public PaymentResponse processPayment(PaymentRequest request, Transaction transaction) {
        log.info("Processing VNPay payment for ticket: {}", request.getTicketId());

        try {
            Map<String, String> vnpParamsMap = this.getVNPayConfig();
            vnpParamsMap.put(
                    "vnp_Amount",
                    request.getAmount().toBigInteger().toString()
            );

            vnpParamsMap.put("vnp_IpAddr", SecurityUtils.getClientIp());

            // Tạo chữ ký sử dụng VNPayUtil
            String queryString = VNPayUtil.getPaymentURL(vnpParamsMap, false);
            String vnpSecureHash = VNPayUtil.hmacSHA512(secretKey, queryString);
            vnpParamsMap.put("vnp_SecureHash", vnpSecureHash);

            // Tạo payment URL sử dụng VNPayUtil
            String paymentUrl = vnp_PayUrl + "?" + VNPayUtil.getPaymentURL(vnpParamsMap, true);

            // Cập nhật transaction
            transaction.setStatus(TransactionStatus.PENDING);
            transaction.setProviderTxnNo(null); // Chưa có từ VNPay

            return PaymentResponse.builder()
                    .transactionId(transaction.getId())
                    .ticketId(request.getTicketId())
                    .amount(request.getAmount())
                    .paymentMethod(PaymentMethod.VNPAY)
                    .status(TransactionStatus.PENDING)
                    .paymentUrl(paymentUrl)
                    .providerTxnRef(transaction.getProviderTxnRef())
                    .message("Redirected to VNPay for payment")
                    .timestamp(System.currentTimeMillis())
                    .build();

        } catch (Exception e) {
            log.error("Error processing VNPay payment", e);
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailReason("Error processing payment: " + e.getMessage());

            return PaymentResponse.builder()
                    .transactionId(transaction.getId())
                    .ticketId(request.getTicketId())
                    .amount(request.getAmount())
                    .paymentMethod(PaymentMethod.VNPAY)
                    .status(TransactionStatus.FAILED)
                    .message("Payment processing failed: " + e.getMessage())
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
    }

    @Override
    public Transaction verifyPaymentCallback(String callbackData) {
        log.info("Verifying VNPay payment callback");

        // Thông thường callback sẽ có format: vnp_SecureHash=...&vnp_TxnRef=...&vnp_Amount=...&vnp_ResponseCode=...
        // Bạn cần parse và verify dữ liệu này

        // Ví dụ:
        // - vnp_ResponseCode = "00" nghĩa là giao dịch thành công
        // - vnp_ResponseCode != "00" nghĩa là giao dịch thất bại

        // Cần implement chi tiết parsing và verification
        return null;
    }

    @Override
    public Transaction refundTransaction(Long transactionId, BigDecimal amount) {
        log.info("Processing VNPay refund for transaction: {}", transactionId);

        // Gọi VNPay API để thực hiện hoàn tiền
        // POST đến: apiUrl + "/refund"
        // Cần include transaction ID, amount, signature

        // Cần implement chi tiết
        return null;
    }

    @Override
    public String checkTransactionStatus(String transactionRef) {
        log.info("Checking VNPay transaction status: {}", transactionRef);

        // Gọi VNPay API để kiểm tra trạng thái
        // Cần implement chi tiết
        return null;
    }

    @Override
    public String getProviderName() {
        return "VNPay";
    }

    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", "2.1.0");
        vnpParamsMap.put("vnp_Command", "pay");
        vnpParamsMap.put("vnp_TmnCode", this.vnp_TmnCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_TxnRef", MathUtils.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang:" + MathUtils.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderType", "other");
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_ReturnUrl", this.vnp_ReturnUrl);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);

        calendar.add(Calendar.MINUTE, expiredTime);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);
        return vnpParamsMap;
    }
}


