package uit.se100.services.authentication;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uit.se100.constants.AppConstant;
import uit.se100.dtos.authentication.ChangePasswordRequest;
import uit.se100.dtos.authentication.LoginRequest;
import uit.se100.dtos.authentication.LoginResponse;
import uit.se100.dtos.authentication.RefreshTokenRequest;
import uit.se100.dtos.authentication.RegisterRequest;
import uit.se100.dtos.authentication.ResetPasswordRequest;
import uit.se100.dtos.user.UserRequest;
import uit.se100.dtos.user.UserResponse;
import uit.se100.entities.authentication.RefreshToken;
import uit.se100.entities.authentication.User;
import uit.se100.entities.authentication.Verification;
import uit.se100.enums.RoleEnum;
import uit.se100.enums.authentication.VerificationType;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.hooks.user.UserHook;
import uit.se100.mappers.employee.EmployeeMapper;
import uit.se100.mappers.passenger.user.PassengerMapper;
import uit.se100.mappers.user.UserMapper;
import uit.se100.repositories.authentication.UserRepository;
import uit.se100.securities.SecurityUtil;
import uit.se100.securities.TokenProvider;
import uit.se100.services.general.MailService;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final UserRepository userRepository;
  private final TokenProvider tokenProvider;
  private final RefreshTokenService refreshTokenService;
  private final UserMapper userMapper;
  private final UserHook userHook;
  private final PasswordEncoder passwordEncoder;
  private final VerificationService verificationService;
  private final MailService mailService;
  private final PassengerMapper passengerMapper;
  private final EmployeeMapper employeeMapper;

  @Override
  public LoginResponse login(LoginRequest request) {
    // 1. ---- Authenticate ----
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(request.getCredentialId(), request.getPassword());
    Authentication authentication =
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    // 2. ---- Set to security holder  ----
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 3. ---- Generate JWT ----
    Long userId = SecurityUtil.getCurrentUserId();
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.DATA_INTEGRITY_VIOLATION));
    String jwt = tokenProvider.generateAccessToken(user);
    String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

    // 4. ---- Response ----
    LoginResponse response = new LoginResponse();
    response.setAccessToken(jwt);
    response.setRefreshToken(refreshToken);
    response.setUser(userMapper.entityToResponse(user));
    if (user.getPassenger() != null) {
      response.setPassenger(passengerMapper.entityToResponse(user.getPassenger()));
    }
    if (user.getEmployee() != null) {
      response.setEmployee(employeeMapper.entityToResponse(user.getEmployee()));
    }
    return response;
  }

  @Override
  public LoginResponse refreshToken(RefreshTokenRequest request) {
    RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
    refreshTokenService.verify(refreshToken);
    String jwt = tokenProvider.generateAccessToken(refreshToken.getUser());
    String newRefreshToken =
        refreshTokenService.createRefreshToken(refreshToken.getUser()).getToken();
    refreshTokenService.delete(refreshToken);
    LoginResponse response = new LoginResponse();
    response.setAccessToken(jwt);
    response.setRefreshToken(newRefreshToken);
    response.setUser(userMapper.entityToResponse(refreshToken.getUser()));
    return response;
  }

  @Override
  public void logout(RefreshTokenRequest request) {
    RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
    refreshTokenService.delete(refreshToken);
  }

  @Override
  public UserResponse register(RegisterRequest request) {
    // 1. Convert RegisterRequest to UserRequest to reuse UserHook
    UserRequest userRequest = new UserRequest();
    userRequest.setEmail(request.getEmail());
    userRequest.setPassword(request.getPassword());
    userRequest.setUsername(request.getUsername());
    userRequest.setPhone(request.getPhone());

    // 2. Validate
    userHook.validateCreate(userRequest, Map.of());

    // 3. Create user
    User user = userMapper.requestToEntity(userRequest);

    // 4. enrich
    userHook.enrichCreate(userRequest, user, Map.of());
    user.setRole(RoleEnum.PASSENGER);
    user = userRepository.save(user);
    return userMapper.entityToResponse(user);
  }

  @Override
  public UserResponse getCurrentUser() {
    Long userId = SecurityUtil.getCurrentUserId();
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.DATA_INTEGRITY_VIOLATION));
    return userMapper.entityToResponse(user);
  }

  @Override
  public void changePassword(ChangePasswordRequest request) {
    Long userId = SecurityUtil.getCurrentUserId();
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
    if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
      throw new ApiException(
          ErrorCode.VALIDATION_ERROR, Map.of("oldPassword", "Invalid current password"));
    }
    user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
  }

  @Override
  public void sendResetPasswordEmail(String email) {
    // 1. ---- Validate account ----
    User user =
        userRepository
            .findOne(
                (root, _, builder) ->
                    builder.equal(builder.lower(root.get("email")), email.toLowerCase()))
            .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
    // 2. ---- Generate code ----
    String code =
        verificationService.generateVerificationCode(
            VerificationType.PASSWORD_RESET,
            AppConstant.RESET_PASSWORD_TOKEN_EXPIRE_SECONDS,
            user.getId());
    // 3. ---- Generate magic link ----
    String resetLink =
        String.format("%s/reset-password?code=%s", AppConstant.FRONTEND_BASE_URL, code);
    // 4. ---- Send email ----
    Map<String, Object> templateParams =
        Map.of("resetUrl", resetLink, "user", user, "username", user.getUsername());
    mailService.sendEmailFromTemplate(
        user.getEmail(),
        AppConstant.RESET_PASSWORD_EMAIL_SUBJECT,
        AppConstant.RESET_PASSWORD_EMAIL_TEMPLATE,
        templateParams);
  }

  @Override
  public void resetPassword(ResetPasswordRequest request) {
    // 1. ---- Validate code ----
    Verification verification =
        verificationService.verifyCode(
            VerificationType.PASSWORD_RESET, request.getVerificationCode());
    User user =
        userRepository
            .findById(verification.getUserId())
            .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));

    // 2. ---- Update password ----
    user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
  }
}
