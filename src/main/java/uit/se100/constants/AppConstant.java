package uit.se100.constants;

import java.math.BigDecimal;

public interface AppConstant {
  long MAX_RESERVED_TICKET_PER_PERSON = 7;
  BigDecimal PRICE_TICKET_ECONOMY = new BigDecimal("100.00");
  BigDecimal PRICE_TICKET_BUSINESS = new BigDecimal("200.00");
  BigDecimal PRICE_TICKET_FIRST_CLASS = new BigDecimal("300.00");

  String ACTIVATION_EMAIL_TEMPLATE = "activation-email";
  String ACTIVATION_EMAIL_SUBJECT = "Activate your account";
  Integer aCTIVATION_TOKEN_EXPIRE_SECONDS = 5 * 60; // 5 minutes
  String RESET_PASSWORD_EMAIL_TEMPLATE = "reset-password-email";
  String RESET_PASSWORD_EMAIL_SUBJECT = "Reset your password";
  Integer RESET_PASSWORD_TOKEN_EXPIRE_SECONDS = 5 * 60; // 5 minutes

  String FRONTEND_BASE_URL = "http://localhost:3000";
}
