package uit.se100.constants;

public interface SecurityConstant {
  String[] PUBLIC_URLS = {
    "/auth/login", "auth/register", "auth/refresh", "/v3/api-docs/**", "/swagger-ui/**"
  };
}
