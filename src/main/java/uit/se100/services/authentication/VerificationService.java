package uit.se100.services.authentication;

import uit.se100.entities.authentication.Verification;
import uit.se100.enums.authentication.VerificationType;

public interface VerificationService {
  Verification findByTypeAndCode(VerificationType type, String code);

  String generateVerificationCode(VerificationType type, Integer expirationTime, Long userId);

  Verification verifyCode(VerificationType type, String code);

  void deleteCode(VerificationType type, String code);
}
