package uit.se100.services.payment.strategy;

import uit.se100.dtos.payment.PaymentRequest;
import uit.se100.dtos.payment.PaymentResponse;
import uit.se100.entities.payment.Transaction;

/**
 * Strategy Pattern - Payment Gateway Strategy Interface
 * <p>
 * Mỗi payment provider (MoMo, VNPay, etc.) sẽ implement interface này
 * để cung cấp các hàm thực hiện thanh toán theo cách riêng của họ.
 */
public interface PaymentStrategy {

    /**
     * Xử lý thanh toán thông qua payment gateway cụ thể
     *
     * @param request     - Thông tin yêu cầu thanh toán
     * @param transaction - Transaction entity để lưu thông tin
     * @return PaymentResponse chứa URL hoặc kết quả thanh toán
     */
    PaymentResponse processPayment(PaymentRequest request, Transaction transaction);

    /**
     * Xác nhận/Callback từ payment gateway
     *
     * @param callbackData - Dữ liệu callback từ payment provider
     * @return Transaction đã được cập nhật trạng thái
     */
    Transaction verifyPaymentCallback(String callbackData);

    /**
     * Hoàn tiền (Refund) giao dịch
     *
     * @param transactionId - ID giao dịch cần hoàn tiền
     * @param amount        - Số tiền hoàn tiền
     * @return Transaction refund mới
     */
    Transaction refundTransaction(Long transactionId);

    /**
     * Kiểm tra trạng thái giao dịch từ payment provider
     *
     * @param transactionRef - Reference của giao dịch từ provider
     * @return Trạng thái giao dịch
     */
    String checkTransactionStatus(String transactionRef);

    /**
     * Lấy tên của payment provider
     *
     * @return Tên payment provider
     */
    String getProviderName();
}

