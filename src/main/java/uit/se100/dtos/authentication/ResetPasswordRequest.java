package uit.se100.dtos.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
  @NotBlank private String verificationCode;
  @NotBlank private String newPassword;
}
