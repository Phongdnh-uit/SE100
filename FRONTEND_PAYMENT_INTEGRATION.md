# Frontend Integration Guide - VNPay & ZaloPay Payment

## 1. Cấu Trúc Component Payment

```jsx
// PaymentComponent.jsx
import React, {useState} from 'react';
import axios from 'axios';

const PaymentComponent = ({ticketId, amount}) => {
    const [paymentMethod, setPaymentMethod] = useState('VNPAY');
    const [isLoading, setIsLoading] = useState(false);

    const handlePayment = async () => {
        setIsLoading(true);
        try {
            const response = await axios.post('/api/v1/payments/create', {
                ticketId: ticketId,
                amount: amount,
                paymentMethod: paymentMethod,
                returnUrl: `${window.location.origin}/payment-return`,
                cancelUrl: `${window.location.origin}/payment-cancel`,
                ipAddress: getUserIpAddress(),
                userAgent: navigator.userAgent
            });

            if (response.data.paymentUrl) {
                // Redirect to payment gateway
                window.location.href = response.data.paymentUrl;
            } else {
                alert('Payment initialization failed');
            }
        } catch (error) {
            console.error('Payment error:', error);
            alert('Error: ' + error.response?.data?.message);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="payment-container">
            <h2>Thanh Toán Vé</h2>

            <div className="payment-info">
                <p>Ticket ID: {ticketId}</p>
                <p>Số tiền: {amount.toLocaleString('vi-VN')} VND</p>
            </div>

            <div className="payment-methods">
                <label>
                    <input
                        type="radio"
                        value="VNPAY"
                        checked={paymentMethod === 'VNPAY'}
                        onChange={(e) => setPaymentMethod(e.target.value)}
                    />
                    <img src="/assets/vnpay-logo.png" alt="VNPay"/>
                    VNPay
                </label>

                <label>
                    <input
                        type="radio"
                        value="ZALOPAY"
                        checked={paymentMethod === 'ZALOPAY'}
                        onChange={(e) => setPaymentMethod(e.target.value)}
                    />
                    <img src="/assets/zalopay-logo.png" alt="ZaloPay"/>
                    ZaloPay
                </label>
            </div>

            <button
                onClick={handlePayment}
                disabled={isLoading}
                className="pay-button"
            >
                {isLoading ? 'Processing...' : 'Thanh Toán'}
            </button>
        </div>
    );
};

export default PaymentComponent;
```

## 2. Payment Return Handler

```jsx
// PaymentReturn.jsx
import React, {useEffect, useState} from 'react';
import {useNavigate, useSearchParams} from 'react-router-dom';
import axios from 'axios';

const PaymentReturn = () => {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const [status, setStatus] = useState('loading');
    const [message, setMessage] = useState('Đang xử lý thanh toán...');

    useEffect(() => {
        const verifyPayment = async () => {
            try {
                // Get all query parameters
                const params = Object.fromEntries(searchParams);

                // Determine payment method from parameters
                if (params.vnp_ResponseCode !== undefined) {
                    // VNPay callback
                    if (params.vnp_ResponseCode === '00') {
                        setStatus('success');
                        setMessage('Thanh toán thành công!');
                        setTimeout(() => navigate('/bookings'), 2000);
                    } else {
                        setStatus('failed');
                        setMessage('Thanh toán thất bại. Vui lòng thử lại.');
                    }
                } else if (params.return_code !== undefined) {
                    // ZaloPay callback
                    if (params.return_code === '1') {
                        setStatus('success');
                        setMessage('Thanh toán thành công!');
                        setTimeout(() => navigate('/bookings'), 2000);
                    } else {
                        setStatus('failed');
                        setMessage('Thanh toán thất bại. Vui lòng thử lại.');
                    }
                } else {
                    setStatus('unknown');
                    setMessage('Không xác định được trạng thái thanh toán');
                }
            } catch (error) {
                console.error('Payment verification error:', error);
                setStatus('error');
                setMessage('Lỗi xác minh thanh toán');
            }
        };

        verifyPayment();
    }, [searchParams, navigate]);

    return (
        <div className="payment-return-container">
            {status === 'loading' && (
                <div className="spinner">Đang xử lý...</div>
            )}

            {status === 'success' && (
                <div className="success-message">
                    <h2>✓ Thành Công</h2>
                    <p>{message}</p>
                    <p>Bạn sẽ được chuyển hướng trong vài giây...</p>
                </div>
            )}

            {status === 'failed' && (
                <div className="failed-message">
                    <h2>✗ Thất Bại</h2>
                    <p>{message}</p>
                    <button onClick={() => navigate('/checkout')}>Thử Lại</button>
                </div>
            )}

            {status === 'error' && (
                <div className="error-message">
                    <h2>⚠ Lỗi</h2>
                    <p>{message}</p>
                    <button onClick={() => navigate('/checkout')}>Quay Lại</button>
                </div>
            )}
        </div>
    );
};

export default PaymentReturn;
```

## 3. CSS Styling

```css
/* Payment.css */

.payment-container {
    max-width: 600px;
    margin: 2rem auto;
    padding: 2rem;
    background: #f5f5f5;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.payment-info {
    background: white;
    padding: 1.5rem;
    border-radius: 6px;
    margin-bottom: 1.5rem;
    border-left: 4px solid #007bff;
}

.payment-info p {
    margin: 0.5rem 0;
    font-size: 1rem;
    color: #333;
}

.payment-methods {
    background: white;
    padding: 1.5rem;
    border-radius: 6px;
    margin-bottom: 1.5rem;
}

.payment-methods label {
    display: flex;
    align-items: center;
    padding: 1rem;
    margin-bottom: 0.5rem;
    border: 2px solid #ddd;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.payment-methods label:hover {
    border-color: #007bff;
    background-color: #f0f8ff;
}

.payment-methods input[type="radio"] {
    margin-right: 1rem;
    cursor: pointer;
}

.payment-methods img {
    height: 30px;
    margin-right: 0.5rem;
}

.payment-methods label input[type="radio"]:checked {
    accent-color: #007bff;
}

.payment-methods label input[type="radio"]:checked + img {
    filter: brightness(1.1);
}

.pay-button {
    width: 100%;
    padding: 1rem;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 6px;
    font-size: 1.1rem;
    font-weight: bold;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.pay-button:hover:not(:disabled) {
    background-color: #0056b3;
}

.pay-button:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}

/* Payment Return Styles */
.payment-return-container {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.spinner {
    color: white;
    font-size: 2rem;
    text-align: center;
}

.success-message,
.failed-message,
.error-message {
    background: white;
    padding: 3rem;
    border-radius: 12px;
    text-align: center;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
    max-width: 500px;
}

.success-message h2 {
    color: #28a745;
    font-size: 2rem;
    margin-bottom: 1rem;
}

.failed-message h2 {
    color: #dc3545;
    font-size: 2rem;
    margin-bottom: 1rem;
}

.error-message h2 {
    color: #ffc107;
    font-size: 2rem;
    margin-bottom: 1rem;
}

.success-message p,
.failed-message p,
.error-message p {
    font-size: 1.1rem;
    color: #666;
    margin: 0.5rem 0;
}

.success-message button,
.failed-message button,
.error-message button {
    margin-top: 1.5rem;
    padding: 0.75rem 2rem;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 6px;
    font-size: 1rem;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.success-message button:hover,
.failed-message button:hover,
.error-message button:hover {
    background-color: #0056b3;
}
```

## 4. Routes Setup (React Router)

```jsx
// App.jsx
import {BrowserRouter, Routes, Route} from 'react-router-dom';
import PaymentComponent from './components/PaymentComponent';
import PaymentReturn from './pages/PaymentReturn';
import CheckoutPage from './pages/CheckoutPage';

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/checkout" element={<CheckoutPage/>}/>
                <Route path="/payment" element={<PaymentComponent/>}/>
                <Route path="/payment-return" element={<PaymentReturn/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
```

## 5. Axios Configuration

```jsx
// axiosConfig.js
import axios from 'axios';

const apiClient = axios.create({
    baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    }
});

// Add authorization token if available
apiClient.interceptors.request.use((config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// Handle response errors
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            // Redirect to login
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default apiClient;
```

## 6. Payment Status Polling (Optional)

Nếu bạn muốn poll status thay vì chờ callback:

```jsx
// usePaymentStatus.js
import {useState, useEffect} from 'react';
import axios from 'axios';

export const usePaymentStatus = (transactionId, pollInterval = 5000) => {
    const [status, setStatus] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!transactionId) return;

        const pollStatus = async () => {
            try {
                const response = await axios.get(`/api/v1/payments/${transactionId}/status`);
                setStatus(response.data);

                // Stop polling if transaction is complete
                if (response.data.status === 'COMPLETED' || response.data.status === 'FAILED') {
                    setLoading(false);
                }
            } catch (err) {
                setError(err.message);
            }
        };

        pollStatus();
        const interval = setInterval(pollStatus, pollInterval);

        return () => clearInterval(interval);
    }, [transactionId, pollInterval]);

    return {status, loading, error};
};

// Usage
const {status, loading} = usePaymentStatus(transactionId);
```

## 7. Error Handling Best Practices

```jsx
// PaymentErrorHandler.jsx
export const handlePaymentError = (error) => {
    if (!error.response) {
        return 'Network error. Please check your connection.';
    }

    const status = error.response.status;
    const data = error.response.data;

    switch (status) {
        case 400:
            return data.message || 'Invalid payment request';
        case 401:
            return 'Please log in to make payment';
        case 404:
            return 'Ticket not found';
        case 500:
            return 'Server error. Please try again later';
        default:
            return 'Payment failed. Please try again.';
    }
};
```

## 8. Environment Configuration

```env
# .env.local
REACT_APP_API_BASE_URL=http://localhost:8080
REACT_APP_PAYMENT_RETURN_URL=http://localhost:3000/payment-return
REACT_APP_PAYMENT_CANCEL_URL=http://localhost:3000/payment-cancel
```

## 9. Testing Payment Flow

### Test VNPay Flow:

1. Truy cập `/checkout`
2. Chọn VNPay
3. Nhập thẻ test: `4111111111111111`
4. Nhập CVV: `123`
5. Nhập OTP: `123456`
6. Kết quả: Thành công hoặc thất bại

### Test ZaloPay Flow:

1. Truy cập `/checkout`
2. Chọn ZaloPay
3. Sử dụng tài khoản test ZaloPay (được cung cấp)
4. Xác nhận thanh toán
5. Kết quả: Thành công hoặc thất bại

## 10. Production Considerations

- **HTTPS Only**: Luôn sử dụng HTTPS trong production
- **Sensitive Data**: Không lưu số thẻ hay CVV trên frontend
- **CORS**: Cấu hình CORS đúng trên backend
- **Rate Limiting**: Implement rate limiting cho payment endpoints
- **Logging**: Log tất cả payment requests/responses (ngoài PCI data)
- **Monitoring**: Monitor payment failures và success rates
- **Webhook Verification**: Luôn verify webhook signatures từ payment gateways


