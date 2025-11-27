package uit.se100.services.authentication;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uit.se100.dtos.authentication.LoginRequest;
import uit.se100.dtos.authentication.LoginResponse;
import uit.se100.dtos.authentication.RefreshTokenRequest;
import uit.se100.dtos.authentication.RegisterRequest;
import uit.se100.dtos.user.UserRequest;
import uit.se100.dtos.user.UserResponse;
import uit.se100.entities.authentication.RefreshToken;
import uit.se100.entities.authentication.User;
import uit.se100.enums.RoleEnum;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.hooks.user.UserHook;
import uit.se100.mappers.user.UserMapper;
import uit.se100.repositories.authentication.UserRepository;
import uit.se100.securities.SecurityUtil;
import uit.se100.securities.TokenProvider;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final UserRepository userRepository;
  private final TokenProvider tokenProvider;
  private final RefreshTokenService refreshTokenService;
  private final UserMapper userMapper;
  private final UserHook userHook;

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
}
