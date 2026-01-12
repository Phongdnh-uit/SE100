package uit.se100.configs.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uit.se100.enums.payments.PaymentMethod;
import uit.se100.services.payment.strategy.PaymentStrategy;
import uit.se100.services.payment.strategy.impl.VNPayPaymentStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * PaymentStrategyConfiguration
 * <p>
 * Configuration để inject Map<PaymentMethod, PaymentStrategy>
 * vào PaymentServiceImpl
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class PaymentStrategyConfiguration {

    private final VNPayPaymentStrategy vnpayPaymentStrategy;
//    private final ZaloPayPaymentStrategy zaloPayPaymentStrategy;

    /**
     * Tạo Map chứa tất cả payment strategies
     * Map này sẽ được inject vào PaymentServiceImpl
     */
    @Bean
    public Map<PaymentMethod, PaymentStrategy> paymentStrategies() {
        Map<PaymentMethod, PaymentStrategy> strategies = new HashMap<>();

        // Đăng ký VNPay strategy
        strategies.put(PaymentMethod.VNPAY, vnpayPaymentStrategy);
        log.info("Registered payment strategy: VNPAY -> {}", vnpayPaymentStrategy.getProviderName());

        // Đăng ký ZaloPay strategy
//        strategies.put(PaymentMethod.ZALOPAY, zaloPayPaymentStrategy);
//        log.info("Registered payment strategy: ZALOPAY -> {}", zaloPayPaymentStrategy.getProviderName());

        log.info("Payment strategies configuration initialized with {} strategies", strategies.size());
        return strategies;
    }
}

