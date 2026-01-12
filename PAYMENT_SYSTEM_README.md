# Hệ Thống Thanh Toán Vé Máy Bay - Strategy Pattern

## Tổng Quan

Hệ thống thanh toán được xây dựng dựa trên **Strategy Pattern** cho phép dễ dàng thêm, sửa, hoặc xóa các phương thức
thanh toán mà không cần sửa đổi code chính.

Hiện tại hỗ trợ:

- **MoMo** - Mobile Payment Gateway
- **VNPay** - Vietnam Payment Gateway
- Có thể dễ dàng mở rộng với: Bank Transfer, Credit Card, Debit Card, ...

## Kiến Trúc (Architecture)

### Strategy Pattern Flow

```
PaymentServiceImpl
    ├── PaymentGatewayRegistry (Quản lý các Strategy)
    │   ├── PaymentStrategy (Interface)
    │   │   ├── MoMoPaymentStrategy (Implementation)
    │   │   └── VNPayPaymentStrategy (Implementation)
    │   └── [Có thể thêm Strategy khác]
    └── PaymentController (API Endpoints)
```

### Main Components

1. **PaymentStrategy** - Interface định nghĩa các phương thức cần implement
2. **MoMoPaymentStrategy** - Implementation cho MoMo
3. **VNPayPaymentStrategy** - Implementation cho VNPay
4. **PaymentServiceImpl** - Service logic sử dụng Strategy Pattern
5. **PaymentGatewayRegistry** - Registry quản lý các Strategy
6. **PaymentController** - REST API endpoints
7. **PaymentRequest/Response** - DTO cho request/response
8. **Transaction** - Entity lưu thông tin giao dịch

## File Structure

```
src/main/java/uit/se100/
├── controllers/
│   └── payment/
│       └── PaymentController.java
├── services/
│   ├── PaymentService.java (Interface)
│   ├── PaymentServiceImpl.java (Implementation)
│   ├── PaymentGatewayRegistry.java (Strategy Registry)
│   └── payment/
│       └── strategy/
│           ├── PaymentStrategy.java (Strategy Interface)
│           └── impl/
│               ├── MoMoPaymentStrategy.java
│               └── VNPayPaymentStrategy.java
├── dtos/
│   └── payment/
│       ├── PaymentRequest.java
│       └── PaymentResponse.java
├── entities/
│   └── payment/
│       └── Transaction.java
├── repositories/
│   └── payment/
│       └── TransactionRepository.java
├── enums/
│   └── payments/
│       ├── PaymentMethod.java (MoMo, VNPay, ...)
│       ├── TransactionStatus.java (PENDING, SUCCESS, FAILED)
│       └── TransactionType.java (TICKET_PAYMENT, REFUND, ...)
└── utils/
    └── VNPayUtil.java (Utility cho VNPay)
```

## Configuration

### application-dev.yaml

```yaml
payment:
  return-url: http://localhost:8080/api/v1/payments/callback
  
  # MoMo Payment Configuration
  momo:
    endpoint: https://test-payment.momo.vn/v3/gateway/api/create
    partnerCode: ${MOMO_PARTNER_CODE:MOMOBKUN20170829}
    accessKey: ${MOMO_ACCESS_KEY:F8590EC41FF86003BBF3DED6D3F91713}
    secretKey: ${MOMO_SECRET_KEY:fa330da3efbb52dae29b16e98f2032b5}
    
  # VNPay Payment Configuration
  vnpay:
    url: https://sandbox.vnpayment.vn/qr
    apiUrl: https://sandbox.vnpayment.vn/merchant_webapi/api/transaction
    tmnCode: ${VNPAY_TMN_CODE:}
    secretKey: ${VNPAY_SECRET_KEY:}
    version: "2.1.0"
    command: pay
    orderType: billpayment
    expiredTime: 15
    returnUrl: http://localhost:8080/api/v1/payments/vnpay-callback
```

### application-prod.yaml

Sử dụng environment variables cho các sensitive information

```yaml
payment:
  return-url: ${PAYMENT_RETURN_URL}
  
  momo:
    endpoint: https://payment.momo.vn/v3/gateway/api/create
    partnerCode: ${MOMO_PARTNER_CODE}
    accessKey: ${MOMO_ACCESS_KEY}
    secretKey: ${MOMO_SECRET_KEY}
    
  vnpay:
    url: https://vnpayment.vn/qr
    apiUrl: https://api.vnpayment.vn/merchant_webapi/api/transaction
    tmnCode: ${VNPAY_TMN_CODE}
    secretKey: ${VNPAY_SECRET_KEY}
    version: "2.1.0"
    command: pay
    orderType: billpayment
    expiredTime: 15
    returnUrl: ${VNPAY_RETURN_URL}
```

## API Endpoints

### 1. Tạo thanh toán cho vé

```http
POST /api/v1/payments/create
Content-Type: application/json

{
  "ticketId": 1,
  "amount": 2500000,
  "paymentMethod": "MOMO",
  "description": "Ticket payment",
  "returnUrl": "https://yourapp.com/payment-callback",
  "cancelUrl": "https://yourapp.com/payment-cancel",
  "ipAddress": "192.168.1.1",
  "userAgent": "Mozilla/5.0..."
}
```

**Response (Success):**

```json
{
  "transactionId": 1,
  "ticketId": 1,
  "amount": 2500000,
  "paymentMethod": "MOMO",
  "status": "PENDING",
  "paymentUrl": "https://test-payment.momo.vn/v3/gateway/api/create?...",
  "providerTxnRef": "TXN_1704960000000_a1b2c3d4",
  "message": "Redirected to MoMo for payment",
  "timestamp": 1704960000000
}
```

### 2. MoMo Callback

```http
GET /api/v1/payments/momo-callback?partnerCode=...&orderId=...&amount=...&transId=...&resultCode=...&signature=...
```

### 3. VNPay Callback

```http
GET /api/v1/payments/vnpay-callback?vnp_TxnRef=...&vnp_Amount=...&vnp_ResponseCode=...&vnp_SecureHash=...
```

### 4. Hoàn tiền

```http
POST /api/v1/payments/{transactionId}/refund
```

### 5. Kiểm tra trạng thái

```http
GET /api/v1/payments/{transactionId}/status
```

### 6. Lấy danh sách phương thức thanh toán

```http
GET /api/v1/payments/methods
```

**Response:**

```json
[
  "MOMO",
  "VNPAY",
  "BANK_TRANSFER",
  "CREDIT_CARD",
  "DEBIT_CARD"
]
```

## Cách Sử Dụng

### 1. Tạo Payment Request

```java
PaymentRequest paymentRequest = PaymentRequest.builder()
    .ticketId(1L)
    .amount(new BigDecimal("2500000"))
    .paymentMethod(PaymentMethod.MOMO)
    .description("Ticket payment")
    .returnUrl("http://localhost:3000/payment-callback")
    .ipAddress("192.168.1.1")
    .userAgent("Mozilla/5.0...")
    .build();

// Gọi service
PaymentResponse response = paymentService.createPaymentForTicket(ticketId, paymentRequest);

// Redirect user đến payment URL
return ResponseEntity.ok(response);
```

### 2. Handle Callback từ Payment Provider

**MoMo Callback:**

```java
// Controller sẽ nhận callback từ MoMo
@GetMapping("/momo-callback")
public ResponseEntity<String> momoCallback(@RequestParam String partnerCode, ...) {
    // Verify signature
    // Update transaction status
    // Return success/failure
}
```

**VNPay Callback:**

```java
@GetMapping("/vnpay-callback")
public ResponseEntity<String> vnpayCallback(@RequestParam String vnp_ResponseCode, ...) {
    // Verify signature
    // Update transaction status
    // Return success/failure
}
```

## Thêm Payment Strategy Mới

Để thêm phương thức thanh toán mới (ví dụ: Stripe, PayPal), làm theo các bước sau:

### 1. Tạo Strategy Class

```java
@Slf4j
@Component
public class StripePaymentStrategy implements PaymentStrategy {
    
    @Override
    public PaymentResponse processPayment(PaymentRequest request, Transaction transaction) {
        // Implementation cho Stripe
    }
    
    @Override
    public Transaction verifyPaymentCallback(String callbackData) {
        // Verify Stripe callback
    }
    
    @Override
    public Transaction refundTransaction(Long transactionId, BigDecimal amount) {
        // Refund logic
    }
    
    @Override
    public String checkTransactionStatus(String transactionRef) {
        // Check status
    }
    
    @Override
    public String getProviderName() {
        return "Stripe";
    }
}
```

### 2. Thêm vào Enum PaymentMethod

```java
public enum PaymentMethod {
    MOMO("MoMo", "momo"),
    VNPAY("VNPay", "vnpay"),
    STRIPE("Stripe", "stripe"),  // Thêm dòng này
    // ...
}
```

### 3. Thêm vào PaymentGatewayRegistry

```java
@Autowired
public PaymentGatewayRegistry(
    MoMoPaymentStrategy momoPaymentStrategy,
    VNPayPaymentStrategy vnpayPaymentStrategy,
    StripePaymentStrategy stripePaymentStrategy  // Inject Stripe strategy
) {
    registerStrategy(PaymentMethod.MOMO, momoPaymentStrategy);
    registerStrategy(PaymentMethod.VNPAY, vnpayPaymentStrategy);
    registerStrategy(PaymentMethod.STRIPE, stripePaymentStrategy);  // Register Stripe
}
```

### 4. Thêm Configuration vào application.yaml

```yaml
payment:
  stripe:
    apiKey: ${STRIPE_API_KEY}
    secretKey: ${STRIPE_SECRET_KEY}
    returnUrl: http://localhost:8080/api/v1/payments/stripe-callback
```

## Error Handling

Hệ thống xử lý các lỗi sau:

1. **Ticket not found** - Vé không tồn tại
2. **Invalid payment method** - Phương thức thanh toán không hỗ trợ
3. **Payment gateway error** - Lỗi kết nối đến payment gateway
4. **Signature verification failed** - Chữ ký callback không hợp lệ
5. **Transaction not found** - Giao dịch không tồn tại

## Best Practices

1. **Security:**
    - Luôn verify signature từ payment provider
    - Không lưu sensitive data (API key, secret key) trong code
    - Sử dụng environment variables cho production
    - Validate tất cả input từ user

2. **Transaction Management:**
    - Sử dụng @Transactional để đảm bảo data consistency
    - Lưu chi tiết giao dịch (provider response, timestamp, etc.)
    - Implement retry logic cho payment gateway failures

3. **Logging:**
    - Log tất cả payment operations
    - Log chi tiết errors để debugging
    - Không log sensitive information (API keys, full card numbers)

4. **Testing:**
    - Sử dụng sandbox environment để test
    - Test callback verification logic
    - Test error scenarios

## Testing

### Sandbox Credentials

**MoMo:**

- Partner Code: `MOMOBKUN20170829`
- Access Key: `F8590EC41FF86003BBF3DED6D3F91713`
- Secret Key: `fa330da3efbb52dae29b16e98f2032b5`
- Endpoint: `https://test-payment.momo.vn/v3/gateway/api/create`

**VNPay:**

- Sandbox URL: `https://sandbox.vnpayment.vn/`
- Cần đăng ký tài khoản để lấy TMN Code và Secret Key

## References

- [MoMo Developer Documentation](https://developers.momo.vn/)
- [VNPay Integration Guide](https://vnpayment.vn/)
- [Strategy Pattern in Java](https://refactoring.guru/design-patterns/strategy)

## Support

Để tìm hiểu thêm hoặc báo lỗi, vui lòng liên hệ team development.

