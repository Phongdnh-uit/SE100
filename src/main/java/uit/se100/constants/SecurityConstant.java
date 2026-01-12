package uit.se100.constants;

public interface SecurityConstant {
    String[] PUBLIC_URLS = {
            "/auth/login", "auth/register", "auth/refresh", "/v3/api-docs/**", "/swagger-ui/**"
    };

    String[] PUBLIC_GET_URLS = {
            "/passengers/{id}",
            "/passengers",
            "/api/v1/payments/vnpay-callback"


    };

    String[] PUBLIC_POST_URLS = {
            "/passengers",
            "/api/v1/payments/vnpay-callback",
            "/auth/send-reset-password-email",
            "/auth/reset-password"
    };

    String[] PUBLIC_PUT_URLS = {

    };

    String[] PUBLIC_PATCH_URLS = {
    };

    String[] PUBLIC_DELETE_URLS = {
            "/passengers/{id}"
    };
}
