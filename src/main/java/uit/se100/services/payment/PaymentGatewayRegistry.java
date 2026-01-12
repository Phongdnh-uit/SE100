package uit.se100.services.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uit.se100.enums.payments.PaymentMethod;
import uit.se100.services.payment.strategy.PaymentStrategy;
import uit.se100.services.payment.strategy.impl.VNPayPaymentStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * PaymentGatewayRegistry
 * <p>
 * Registry để quản lý các payment strategy
 * Dễ dàng thêm, sửa, hoặc xóa payment strategies
 */
@Slf4j
@Component
public class PaymentGatewayRegistry {

    private final Map<PaymentMethod, PaymentStrategy> strategies = new HashMap<>();

    @Autowired
    public PaymentGatewayRegistry(
            VNPayPaymentStrategy vnpayPaymentStrategy
//            ZaloPayPaymentStrategy zaloPayPaymentStrategy
    ) {
        registerStrategy(PaymentMethod.VNPAY, vnpayPaymentStrategy);
//        registerStrategy(PaymentMethod.ZALOPAY, zaloPayPaymentStrategy);

        log.info("Payment Gateway Registry initialized with {} strategies", strategies.size());
    }

    /**
     * Đăng ký một payment strategy
     */
    public void registerStrategy(PaymentMethod method, PaymentStrategy strategy) {
        strategies.put(method, strategy);
        log.info("Registered payment strategy: {} -> {}", method, strategy.getProviderName());
    }

    /**
     * Lấy payment strategy
     */
    public PaymentStrategy getStrategy(PaymentMethod method) {
        PaymentStrategy strategy = strategies.get(method);
        if (strategy == null) {
            throw new IllegalArgumentException("Payment strategy not found: " + method);
        }
        return strategy;
    }

    /**
     * Kiểm tra xem có strategy nào cho payment method không
     */
    public boolean hasStrategy(PaymentMethod method) {
        return strategies.containsKey(method);
    }

    /**
     * Lấy tất cả payment methods có hỗ trợ
     */
    public Map<PaymentMethod, PaymentStrategy> getAllStrategies() {
        return new HashMap<>(strategies);
    }

    /**
     * Lấy số lượng payment strategies
     */
    public int getStrategyCount() {
        return strategies.size();
    }
}

