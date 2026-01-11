package uit.se100.services.authentication;

import uit.se100.dtos.authentication.ChangePasswordRequest;
import uit.se100.dtos.authentication.CurrentUserResponse;
import uit.se100.dtos.authentication.LoginRequest;
import uit.se100.dtos.authentication.LoginResponse;
import uit.se100.dtos.authentication.RefreshTokenRequest;
import uit.se100.dtos.authentication.RegisterRequest;
import uit.se100.dtos.authentication.ResetPasswordRequest;
import uit.se100.dtos.user.UserResponse;

public interface AuthService {
  LoginResponse login(LoginRequest request);

  LoginResponse refreshToken(RefreshTokenRequest request);

  void logout(RefreshTokenRequest request);

  UserResponse register(RegisterRequest request);

  void sendResetPasswordEmail(String email);

  void resetPassword(ResetPasswordRequest request);

  void changePassword(ChangePasswordRequest request);

  CurrentUserResponse getCurrentUser();
}
