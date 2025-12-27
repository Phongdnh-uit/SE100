package uit.se100.controllers.authentication;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uit.se100.dtos.ApiResponse;
import uit.se100.dtos.authentication.ChangePasswordRequest;
import uit.se100.dtos.authentication.LoginRequest;
import uit.se100.dtos.authentication.LoginResponse;
import uit.se100.dtos.authentication.RefreshTokenRequest;
import uit.se100.dtos.authentication.RegisterRequest;
import uit.se100.dtos.user.UserResponse;
import uit.se100.services.authentication.AuthService;

@Tag(name = "Auth")
@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponse>> login(
      @Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(ApiResponse.ok(authService.login(request)));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
      @Valid @RequestBody RefreshTokenRequest request) {
    return ResponseEntity.ok(ApiResponse.ok(authService.refreshToken(request)));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<LoginResponse>> logout(
      @Valid @RequestBody RefreshTokenRequest request) {
    authService.logout(request);
    return ResponseEntity.ok(ApiResponse.ok(null));
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserResponse>> register(
      @Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(ApiResponse.ok(authService.register(request)));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
    return ResponseEntity.ok(ApiResponse.ok(authService.getCurrentUser()));
  }

  @PostMapping("/change-password")
  public ResponseEntity<ApiResponse<Void>> changePassword(
      @Valid @RequestBody ChangePasswordRequest request) {
    authService.changePassword(request);
    return ResponseEntity.ok(ApiResponse.ok(null));
  }
}
