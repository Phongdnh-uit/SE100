//package uit.se100.services.payment.strategy.impl;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import uit.se100.dtos.payment.PaymentRequest;
//import uit.se100.dtos.payment.PaymentResponse;
//import uit.se100.entities.payment.Transaction;
//import uit.se100.enums.payments.PaymentMethod;
//import uit.se100.enums.payments.TransactionStatus;
//import uit.se100.services.payment.strategy.PaymentStrategy;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.math.BigDecimal;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
/// **
// * ZaloPay Payment Strategy Implementation
// * <p>
// * Hỗ trợ thanh toán qua ZaloPay gateway
// * Tham khảo: https://developers.zalopay.vn/ (Developers ZaloPay)
// */
//@Slf4j
//@Component
//public class ZaloPayPaymentStrategy implements PaymentStrategy {
//
//    @Value("${payment.zalopay.app-id:}")
//    private String appId;
//
//    @Value("${payment.zalopay.key1:}")
//    private String key1;
//
//    @Value("${payment.zalopay.url:https://sandbox.zalopay.com.vn/api/v2/create}")
//    private String createOrderUrl;
//
//    @Value("${payment.return-url:}")
//    private String returnUrl;
//
//    @Override
//    public PaymentResponse processPayment(PaymentRequest request, Transaction transaction) {
//        log.info("Processing ZaloPay payment for ticket: {}", request.getTicketId());
//
//        try {
//            // Tạo order data cho ZaloPay
//            Map<String, Object> orderData = createZaloPayOrder(request, transaction);
//
//            // Tính toán MAC (Message Authentication Code)
//            String mac = computeHmacSHA256(orderData);
//
//            // Tạo payment URL
//            String paymentUrl = buildPaymentUrl(orderData, mac);
//
//            // Cập nhật transaction
//            transaction.setStatus(TransactionStatus.PENDING);
//
//            return PaymentResponse.builder()
//                    .transactionId(transaction.getId())
//                    .ticketId(request.getTicketId())
//                    .amount(request.getAmount())
//                    .paymentMethod(PaymentMethod.ZALOPAY)
//                    .status(TransactionStatus.PENDING)
//                    .paymentUrl(paymentUrl)
//                    .providerTxnRef(transaction.getProviderTxnRef())
//                    .message("Redirected to ZaloPay for payment")
//                    .timestamp(System.currentTimeMillis())
//                    .build();
//
//        } catch (Exception e) {
//            log.error("Error processing ZaloPay payment", e);
//            transaction.setStatus(TransactionStatus.FAILED);
//            transaction.setFailReason("Error processing payment: " + e.getMessage());
//
//            return PaymentResponse.builder()
//                    .transactionId(transaction.getId())
//                    .ticketId(request.getTicketId())
//                    .amount(request.getAmount())
//                    .paymentMethod(PaymentMethod.ZALOPAY)
//                    .status(TransactionStatus.FAILED)
//                    .message("Payment processing failed: " + e.getMessage())
//                    .timestamp(System.currentTimeMillis())
//                    .build();
//        }
//    }
//
//    @Override
//    public Transaction verifyPaymentCallback(String callbackData) {
//        log.info("Verifying ZaloPay payment callback");
//
//        try {
//            // Parse callback data từ ZaloPay
//            // ZaloPay callback sẽ chứa:
//            // - return_code: 1 = thành công, != 1 = thất bại
//            // - zp_trans_id: transaction ID từ ZaloPay
//            // - mac: Message Authentication Code
//
//            // Cần parse và verify dữ liệu này
//            // Ví dụ về format callback từ ZaloPay:
//            /*
//            {
//              "return_code": 1,
//              "return_message": "success",
//              "sub_return_code": 1,
//              "sub_return_message": "success",
//              "zp_trans_id": 123456789,
//              "server_time": 1234567890,
//              "amount": 50000,
//              "discount_amount": 0
//            }
//            */
//
//            log.info("Callback data: {}", callbackData);
//
//            // Cần implement chi tiết parsing và verification
//            return null;
//
//        } catch (Exception e) {
//            log.error("Error verifying ZaloPay callback", e);
//            return null;
//        }
//    }
//
//    @Override
//    public Transaction refundTransaction(Long transactionId, BigDecimal amount) {
//        log.info("Processing ZaloPay refund for transaction: {}", transactionId);
//
//        try {
//            // Gọi ZaloPay API để thực hiện hoàn tiền
//            // POST đến: /v2/refund endpoint
//
//            // Cần implement chi tiết
//            return null;
//
//        } catch (Exception e) {
//            log.error("Error processing ZaloPay refund", e);
//            throw new RuntimeException("Refund failed: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public String checkTransactionStatus(String transactionRef) {
//        log.info("Checking ZaloPay transaction status: {}", transactionRef);
//
//        try {
//            // Gọi ZaloPay API để kiểm tra trạng thái
//            // POST đến: /v2/query endpoint
//
//            // Cần implement chi tiết
//            return null;
//
//        } catch (Exception e) {
//            log.error("Error checking ZaloPay transaction status", e);
//            throw new RuntimeException("Status check failed: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public String getProviderName() {
//        return "ZaloPay";
//    }
//
//    /**
//     * Tạo ZaloPay order data
//     */
//    private Map<String, Object> createZaloPayOrder(PaymentRequest request, Transaction transaction) {
//        Map<String, Object> orderData = new LinkedHashMap<>();
//
//        long amountInVND = request.getAmount().longValue();
//        long timestamp = System.currentTimeMillis();
//
//        orderData.put("app_id", appId);
//        orderData.put("app_trans_id", timestamp + "_" + transaction.getProviderTxnRef());
//        orderData.put("app_user", "user_" + request.getTicketId());
//        orderData.put("amount", amountInVND);
//        orderData.put("app_time", timestamp);
//        orderData.put("expire_time", timestamp + (15 * 60 * 1000)); // 15 phút
//        orderData.put("item", "[]");
//        orderData.put("description", "Thanh toan ve may bay - Ticket ID: " + request.getTicketId());
//        orderData.put("bank_code", "");
//        orderData.put("callback_url", returnUrl != null ? returnUrl : request.getReturnUrl());
//
//        return orderData;
//    }
//
//    /**
//     * Tính toán HMAC SHA256 cho ZaloPay
//     */
//    private String computeHmacSHA256(Map<String, Object> orderData) {
//        try {
//            // Format data theo thứ tự key1|app_id|app_trans_id|amount|app_user|expire_time|item|description|bank_code
//            String dataString = String.format(
//                    "%s|%s|%s|%s|%s|%s|%s|%s|%s",
//                    key1,
//                    orderData.get("app_id"),
//                    orderData.get("app_trans_id"),
//                    orderData.get("amount"),
//                    orderData.get("app_user"),
//                    orderData.get("expire_time"),
//                    orderData.get("item"),
//                    orderData.get("description"),
//                    orderData.get("bank_code")
//            );
//
//            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
//            SecretKeySpec secretKey = new SecretKeySpec(key1.getBytes(), "HmacSHA256");
//            hmacSha256.init(secretKey);
//
//            byte[] hash = hmacSha256.doFinal(dataString.getBytes());
//            return bytesToHex(hash);
//
//        } catch (Exception e) {
//            log.error("Error computing HMAC SHA256", e);
//            throw new RuntimeException("Error computing HMAC: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Chuyển đổi byte array thành hex string
//     */
//    private String bytesToHex(byte[] bytes) {
//        StringBuilder sb = new StringBuilder();
//        for (byte b : bytes) {
//            sb.append(String.format("%02x", b));
//        }
//        return sb.toString();
//    }
//
//    /**
//     * Tạo payment URL redirect đến ZaloPay
//     */
//    private String buildPaymentUrl(Map<String, Object> orderData, String mac) {
//        return createOrderUrl +
//                "?app_id=" + orderData.get("app_id") +
//                "&app_trans_id=" + orderData.get("app_trans_id") +
//                "&amount=" + orderData.get("amount") +
//                "&item=" + orderData.get("item") +
//                "&description=" + orderData.get("description") +
//                "&user_id=" + orderData.get("app_user") +
//                "&mac=" + mac;
//    }
//}
//
