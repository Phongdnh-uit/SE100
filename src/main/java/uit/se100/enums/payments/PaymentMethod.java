package uit.se100.enums.payments;

public enum PaymentMethod {
    VNPAY("VNPay", "vnpay");
//    ZALOPAY("ZaloPay", "zalopay");

    private final String displayName;
    private final String code;

    PaymentMethod(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public static PaymentMethod fromCode(String code) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.code.equalsIgnoreCase(code)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown payment method: " + code);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }
}

