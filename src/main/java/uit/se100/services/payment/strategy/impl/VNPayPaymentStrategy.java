package uit.se100.services.payment.strategy.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import uit.se100.dtos.payment.PaymentRequest;
import uit.se100.dtos.payment.PaymentResponse;
import uit.se100.entities.payment.Transaction;
import uit.se100.enums.payments.PaymentMethod;
import uit.se100.enums.payments.TransactionStatus;
import uit.se100.enums.payments.TransactionType;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.repositories.payment.TransactionRepository;
import uit.se100.repositories.ticket.TicketRepository;
import uit.se100.services.payment.strategy.PaymentStrategy;
import uit.se100.services.ticket.TicketEmailService;
import uit.se100.utils.MathUtils;
import uit.se100.utils.SecurityUtils;
import uit.se100.utils.VNPayUtil;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
@RequiredArgsConstructor
public class VNPayPaymentStrategy implements PaymentStrategy {
    private final TicketRepository ticketRepository;
    private final TicketEmailService ticketEmailService;
    private final TransactionRepository transactionRepository;
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

            vnpParamsMap.put("vnp_TxnRef", transaction.getProviderTxnRef());

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
        if (callbackData == null || callbackData.trim().isEmpty()) {
            throw new ApiException(ErrorCode.TOKEN_INVALID, "Callback data is empty");
        }

        log.info("VNPay callback received: {}", callbackData);

        // Bước 1: Parse query string thành Map
        Map<String, String> params = parseQueryString(callbackData);

        // Bước 2: Lấy và kiểm tra secure hash
        String receivedSecureHash = params.remove("vnp_SecureHash");
        if (receivedSecureHash == null || receivedSecureHash.isEmpty()) {
            throw new ApiException(ErrorCode.SIGNATURE_INVALID, "Missing vnp_SecureHash");
        }

        // Sắp xếp và tạo chuỗi sign data (theo thứ tự alphabet)
        String signData = VNPayUtil.getPaymentURL(params, false); // false = không encode lần nữa

        // Tính hash bằng HMAC-SHA512 (chuẩn VNPay 2.1.0+)
        String calculatedHash = VNPayUtil.hmacSHA512(secretKey, signData);

        if (!calculatedHash.equals(receivedSecureHash)) {
            log.error("Invalid VNPay secure hash! Calculated: {}, Received: {}", calculatedHash, receivedSecureHash);
            throw new ApiException(ErrorCode.SIGNATURE_INVALID, "Invalid secure hash - possible tampering");
        }

        // Bước 3: Lấy các thông tin quan trọng
        String vnp_TxnRef = params.get("vnp_TxnRef");
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String vnp_TransactionStatus = params.get("vnp_TransactionStatus"); // ưu tiên dùng cái này nếu có
        String vnp_AmountStr = params.get("vnp_Amount");
        String vnp_TransactionNo = params.get("vnp_TransactionNo");

        if (vnp_TxnRef == null || vnp_ResponseCode == null) {
            throw new ApiException(ErrorCode.OPERATION_NOT_ALLOWED, "Missing required VNPay parameters");
        }

        // Bước 4: Tìm transaction
        Transaction transaction = transactionRepository.findByProviderTxnRef(vnp_TxnRef)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Transaction not found with ref: " + vnp_TxnRef));

        // Bước 5: Kiểm tra số tiền (bảo vệ chống giả mạo)
        BigDecimal callbackAmount = new BigDecimal(vnp_AmountStr).divide(BigDecimal.valueOf(100));
        if (callbackAmount.compareTo(transaction.getAmount()) != 0) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailReason("Amount mismatch: expected " + transaction.getAmount() + ", received " + callbackAmount);
            transactionRepository.save(transaction);
            throw new ApiException(ErrorCode.TOKEN_INVALID, "Payment amount does not match");
        }

        // Bước 6: Xử lý trạng thái
        String finalStatusCode = (vnp_TransactionStatus != null) ? vnp_TransactionStatus : vnp_ResponseCode;

        if ("00".equals(finalStatusCode)) {
            if (transaction.getStatus() != TransactionStatus.SUCCESS) {
                transaction.setStatus(TransactionStatus.SUCCESS);
                transaction.setProviderTxnNo(vnp_TransactionNo);
                transaction.setUpdatedAt(new Date().toInstant());
                log.info("VNPay payment SUCCESS for txnRef: {}", vnp_TxnRef);

                ticketEmailService.sendPaymentSuccessEmail(transaction.getTicket().getId());
            }
        } else {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailReason("VNPay failed: " + finalStatusCode + " - " + getVNPayResponseMessage(finalStatusCode));
            transaction.setUpdatedAt(new Date().toInstant());
            log.warn("VNPay payment FAILED for txnRef: {} - code: {}", vnp_TxnRef, finalStatusCode);
        }

        transactionRepository.save(transaction);
        return transaction;
    }

    /**
     * Parse query string thành Map<String, String>
     * Xử lý cả trường hợp value chứa dấu &
     */
    private Map<String, String> parseQueryString(String query) {
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("=", 2))
                .filter(arr -> arr.length == 2)
                .collect(Collectors.toMap(
                        arr -> URLDecoder.decode(arr[0], StandardCharsets.UTF_8),
                        arr -> URLDecoder.decode(arr[1], StandardCharsets.UTF_8),
                        (v1, v2) -> v1 // nếu trùng key thì lấy cái đầu
                ));
    }

    private String getVNPayResponseMessage(String code) {
        return switch (code) {
            case "00" -> "Giao dịch thành công";
            case "07" -> "Trừ tiền thành công. Giao dịch bị nghi ngờ";
            case "09" -> "Thẻ/Tài khoản chưa đăng ký Internet Banking";
            case "10" -> "Thẻ/Tài khoản bị khóa";
            case "11" -> "Thẻ/Tài khoản hết hạn";
            case "12" -> "Ngày phát hành/Hết hạn không đúng";
            case "13" -> "Vượt quá hạn mức thanh toán";
            case "24" -> "Khách hàng hủy giao dịch";
            case "51" -> "Số dư không đủ";
            default -> "Lỗi không xác định: " + code;
        };
    }

    private Transaction handlePayTicketSuccessful(String vnp_TxnRef) {
        Transaction transaction = transactionRepository.findByProviderTxnRef(vnp_TxnRef).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Transaction Not Found"));

        transaction.setStatus(TransactionStatus.SUCCESS);

        return transaction;
    }

    private Transaction handlePayTicketFail(String vnp_TxnRef) {
        Transaction transaction = transactionRepository.findByProviderTxnRef(vnp_TxnRef).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Transaction Not Found"));

        transaction.setStatus(TransactionStatus.FAILED);

        return transaction;
    }

    @Override
    public Transaction refundTransaction(Long transactionId) {
        log.info("Processing VNPay refund for transaction: {}", transactionId);

        Transaction oldTransaction = transactionRepository.findById(transactionId).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Transaction Not Found"));

        // Gọi VNPay API để thực hiện hoàn tiền
        // POST đến: apiUrl + "/refund"

        //New Transaction record
        Transaction newTransaction = new Transaction();

        newTransaction.setAmount(calculateRefundAmount(oldTransaction.getAmount()));
        newTransaction.setStatus(TransactionStatus.PENDING);
        newTransaction.setProviderTxnRef(oldTransaction.getProviderTxnRef());
        newTransaction.setType(TransactionType.REFUND);

        transactionRepository.save(newTransaction);

        //send email after success

        try {
            ticketEmailService.sendRefundRequestSuccessEmail(newTransaction);
        } catch (Exception e) {
            log.error("Failed to send refund request success email for transaction {}",
                    newTransaction.getId(), e);
        }
        return null;
    }

    private BigDecimal calculateRefundAmount(BigDecimal amount) {
        return amount;
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


