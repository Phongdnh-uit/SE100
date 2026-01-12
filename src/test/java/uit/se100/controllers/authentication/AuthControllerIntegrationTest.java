package uit.se100.controllers.authentication;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import uit.se100.BaseIntegrationTest;

@AutoConfigureMockMvc
class AuthControllerIntegrationTest extends BaseIntegrationTest {

  //  @Autowired private MockMvc mockMvc;
  //
  //  @Autowired private ObjectMapper objectMapper;
  //
  //  @Autowired private UserRepository userRepository;
  //
  //  @Autowired private RefreshTokenRepository refreshTokenRepository;
  //
  //  @Autowired private PasswordEncoder passwordEncoder;
  //
  //  private static final String TEST_USERNAME = "testuser";
  //  private static final String TEST_EMAIL = "testuser@example.com";
  //  private static final String TEST_PHONE = "0123456789";
  //  private static final String TEST_PASSWORD = "password123";
  //
  //  @BeforeEach
  //  void setUp() {
  //    // Clear in correct order due to foreign key constraints
  //    refreshTokenRepository.deleteAll();
  //    userRepository.deleteAll();
  //  }
  //
  //  private User createAndSaveUser(String username, String email, String phone, String password,
  // RoleEnum role) {
  //    User user = new User();
  //    user.setUsername(username);
  //    user.setEmail(email);
  //    user.setPhone(phone);
  //    user.setPasswordHash(passwordEncoder.encode(password));
  //    user.setRole(role);
  //    return userRepository.save(user);
  //  }
  //
  //  private RegisterRequest createRegisterRequest(String username, String email, String phone,
  // String password) {
  //    RegisterRequest request = new RegisterRequest();
  //    request.setUsername(username);
  //    request.setEmail(email);
  //    request.setPhone(phone);
  //    request.setPassword(password);
  //    return request;
  //  }
  //
  //  private LoginRequest createLoginRequest(String credentialId, String password) {
  //    LoginRequest request = new LoginRequest();
  //    request.setCredentialId(credentialId);
  //    request.setPassword(password);
  //    return request;
  //  }
  //
  //  private RefreshTokenRequest createRefreshTokenRequest(String refreshToken) {
  //    RefreshTokenRequest request = new RefreshTokenRequest();
  //    request.setRefreshToken(refreshToken);
  //    return request;
  //  }
  //
  //  private ChangePasswordRequest createChangePasswordRequest(String oldPassword, String
  // newPassword) {
  //    ChangePasswordRequest request = new ChangePasswordRequest();
  //    request.setOldPassword(oldPassword);
  //    request.setNewPassword(newPassword);
  //    return request;
  //  }
  //
  //  private String loginAndGetAccessToken(String credentialId, String password) throws Exception {
  //    LoginRequest loginRequest = createLoginRequest(credentialId, password);
  //    MvcResult result = mockMvc
  //        .perform(
  //            post("/auth/login")
  //                .contentType(MediaType.APPLICATION_JSON)
  //                .content(objectMapper.writeValueAsString(loginRequest)))
  //        .andExpect(status().isOk())
  //        .andReturn();
  //
  //    return objectMapper
  //        .readTree(result.getResponse().getContentAsString())
  //        .path("data")
  //        .path("accessToken")
  //        .asText();
  //  }
  //
  //  private String loginAndGetRefreshToken(String credentialId, String password) throws Exception
  // {
  //    LoginRequest loginRequest = createLoginRequest(credentialId, password);
  //    MvcResult result = mockMvc
  //        .perform(
  //            post("/auth/login")
  //                .contentType(MediaType.APPLICATION_JSON)
  //                .content(objectMapper.writeValueAsString(loginRequest)))
  //        .andExpect(status().isOk())
  //        .andReturn();
  //
  //    return objectMapper
  //        .readTree(result.getResponse().getContentAsString())
  //        .path("data")
  //        .path("refreshToken")
  //        .asText();
  //  }
  //
  //  @Nested
  //  @DisplayName("POST /auth/register")
  //  @Disabled("Registration may require email/phone verification - bypassed by creating accounts
  // directly in DB")
  //  class RegisterTests {
  //
  //    @Test
  //    @DisplayName("Should register new user successfully")
  //    void shouldRegisterNewUserSuccessfully() throws Exception {
  //      RegisterRequest request = createRegisterRequest(TEST_USERNAME, TEST_EMAIL, TEST_PHONE,
  // TEST_PASSWORD);
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/register")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.code", is(1000)))
  //          .andExpect(jsonPath("$.message", is("success")))
  //          .andExpect(jsonPath("$.data.username", is(TEST_USERNAME)))
  //          .andExpect(jsonPath("$.data.email", is(TEST_EMAIL)))
  //          .andExpect(jsonPath("$.data.phone", is(TEST_PHONE)))
  //          .andExpect(jsonPath("$.data.role", is("PASSENGER")))
  //          .andExpect(jsonPath("$.data.id", notNullValue()));
  //    }
  //
  //    @Test
  //    @DisplayName("Should return validation error when username is blank")
  //    void shouldReturnValidationErrorWhenUsernameIsBlank() throws Exception {
  //      RegisterRequest request = createRegisterRequest("", TEST_EMAIL, TEST_PHONE,
  // TEST_PASSWORD);
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/register")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isBadRequest());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return validation error when email is invalid")
  //    void shouldReturnValidationErrorWhenEmailIsInvalid() throws Exception {
  //      RegisterRequest request = createRegisterRequest(TEST_USERNAME, "invalid-email",
  // TEST_PHONE, TEST_PASSWORD);
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/register")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isBadRequest());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return validation error when phone is blank")
  //    void shouldReturnValidationErrorWhenPhoneIsBlank() throws Exception {
  //      RegisterRequest request = createRegisterRequest(TEST_USERNAME, TEST_EMAIL, "",
  // TEST_PASSWORD);
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/register")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isBadRequest());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return validation error when password is blank")
  //    void shouldReturnValidationErrorWhenPasswordIsBlank() throws Exception {
  //      RegisterRequest request = createRegisterRequest(TEST_USERNAME, TEST_EMAIL, TEST_PHONE,
  // "");
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/register")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isBadRequest());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return error when email already exists")
  //    void shouldReturnErrorWhenEmailAlreadyExists() throws Exception {
  //      // Create existing user
  //      createAndSaveUser("existinguser", TEST_EMAIL, "0987654321", TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //
  //      RegisterRequest request = createRegisterRequest(TEST_USERNAME, TEST_EMAIL, TEST_PHONE,
  // TEST_PASSWORD);
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/register")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isBadRequest());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return error when username already exists")
  //    void shouldReturnErrorWhenUsernameAlreadyExists() throws Exception {
  //      // Create existing user
  //      createAndSaveUser(TEST_USERNAME, "existing@example.com", "0987654321", TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //
  //      RegisterRequest request = createRegisterRequest(TEST_USERNAME, TEST_EMAIL, TEST_PHONE,
  // TEST_PASSWORD);
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/register")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isBadRequest());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return error when phone already exists")
  //    void shouldReturnErrorWhenPhoneAlreadyExists() throws Exception {
  //      // Create existing user
  //      createAndSaveUser("existinguser", "existing@example.com", TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //
  //      RegisterRequest request = createRegisterRequest(TEST_USERNAME, TEST_EMAIL, TEST_PHONE,
  // TEST_PASSWORD);
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/register")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isBadRequest());
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("POST /auth/login")
  //  class LoginTests {
  //
  //    @Test
  //    @DisplayName("Should login successfully with username")
  //    void shouldLoginSuccessfullyWithUsername() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      LoginRequest request = createLoginRequest(TEST_USERNAME, TEST_PASSWORD);
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/login")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.code", is(1000)))
  //          .andExpect(jsonPath("$.message", is("success")))
  //          .andExpect(jsonPath("$.data.accessToken", notNullValue()))
  //          .andExpect(jsonPath("$.data.refreshToken", notNullValue()))
  //          .andExpect(jsonPath("$.data.user.username", is(TEST_USERNAME)))
  //          .andExpect(jsonPath("$.data.user.email", is(TEST_EMAIL)))
  //          .andExpect(jsonPath("$.data.user.role", is("PASSENGER")));
  //    }
  //
  //    @Test
  //    @DisplayName("Should login successfully with email")
  //    void shouldLoginSuccessfullyWithEmail() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      LoginRequest request = createLoginRequest(TEST_EMAIL, TEST_PASSWORD);
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/login")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.code", is(1000)))
  //          .andExpect(jsonPath("$.data.accessToken", notNullValue()))
  //          .andExpect(jsonPath("$.data.refreshToken", notNullValue()))
  //          .andExpect(jsonPath("$.data.user.email", is(TEST_EMAIL)));
  //    }
  //
  //    @Test
  //    @DisplayName("Should login successfully with phone")
  //    void shouldLoginSuccessfullyWithPhone() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      LoginRequest request = createLoginRequest(TEST_PHONE, TEST_PASSWORD);
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/login")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.code", is(1000)))
  //          .andExpect(jsonPath("$.data.accessToken", notNullValue()))
  //          .andExpect(jsonPath("$.data.refreshToken", notNullValue()))
  //          .andExpect(jsonPath("$.data.user.phone", is(TEST_PHONE)));
  //    }
  //
  //    @Test
  //    @DisplayName("Should return error when credentials are invalid")
  //    void shouldReturnErrorWhenCredentialsAreInvalid() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      LoginRequest request = createLoginRequest(TEST_USERNAME, "wrongpassword");
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/login")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isUnauthorized());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return error when user does not exist")
  //    void shouldReturnErrorWhenUserDoesNotExist() throws Exception {
  //      LoginRequest request = createLoginRequest("nonexistent", TEST_PASSWORD);
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/login")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isUnauthorized());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return validation error when credentialId is blank")
  //    void shouldReturnValidationErrorWhenCredentialIdIsBlank() throws Exception {
  //      LoginRequest request = createLoginRequest("", TEST_PASSWORD);
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/login")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isBadRequest());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return validation error when password is blank")
  //    void shouldReturnValidationErrorWhenPasswordIsBlank() throws Exception {
  //      LoginRequest request = createLoginRequest(TEST_USERNAME, "");
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/login")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isBadRequest());
  //    }
  //
  //    @Test
  //    @DisplayName("Should login with different roles")
  //    void shouldLoginWithDifferentRoles() throws Exception {
  //      createAndSaveUser("admin", "admin@example.com", "0111111111", TEST_PASSWORD,
  // RoleEnum.ADMIN);
  //      createAndSaveUser("employee", "employee@example.com", "0222222222", TEST_PASSWORD,
  // RoleEnum.EMPLOYEE);
  //
  //      // Admin login
  //      mockMvc
  //          .perform(
  //              post("/auth/login")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(createLoginRequest("admin",
  // TEST_PASSWORD))))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.data.user.role", is("ADMIN")));
  //
  //      // Employee login
  //      mockMvc
  //          .perform(
  //              post("/auth/login")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(createLoginRequest("employee",
  // TEST_PASSWORD))))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.data.user.role", is("EMPLOYEE")));
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("POST /auth/refresh")
  //  class RefreshTokenTests {
  //
  //    @Test
  //    @DisplayName("Should refresh token successfully")
  //    void shouldRefreshTokenSuccessfully() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      String refreshToken = loginAndGetRefreshToken(TEST_USERNAME, TEST_PASSWORD);
  //
  //      RefreshTokenRequest request = createRefreshTokenRequest(refreshToken);
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/refresh")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.code", is(1000)))
  //          .andExpect(jsonPath("$.data.accessToken", notNullValue()))
  //          .andExpect(jsonPath("$.data.refreshToken", notNullValue()))
  //          .andExpect(jsonPath("$.data.user.username", is(TEST_USERNAME)));
  //    }
  //
  //    @Test
  //    @DisplayName("Should return new tokens when refreshing")
  //    void shouldReturnNewTokensWhenRefreshing() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      String originalRefreshToken = loginAndGetRefreshToken(TEST_USERNAME, TEST_PASSWORD);
  //
  //      RefreshTokenRequest request = createRefreshTokenRequest(originalRefreshToken);
  //
  //      MvcResult result = mockMvc
  //          .perform(
  //              post("/auth/refresh")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isOk())
  //          .andReturn();
  //
  //      String newRefreshToken = objectMapper
  //          .readTree(result.getResponse().getContentAsString())
  //          .path("data")
  //          .path("refreshToken")
  //          .asText();
  //
  //      // New token should be different from original
  //      org.junit.jupiter.api.Assertions.assertNotEquals(originalRefreshToken, newRefreshToken);
  //    }
  //
  //    @Test
  //    @DisplayName("Should invalidate old refresh token after refresh")
  //    void shouldInvalidateOldRefreshTokenAfterRefresh() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      String originalRefreshToken = loginAndGetRefreshToken(TEST_USERNAME, TEST_PASSWORD);
  //
  //      // First refresh - should work
  //      mockMvc
  //          .perform(
  //              post("/auth/refresh")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //
  // .content(objectMapper.writeValueAsString(createRefreshTokenRequest(originalRefreshToken))))
  //          .andExpect(status().isOk());
  //
  //      // Second refresh with same token - should fail (token invalidated)
  //      mockMvc
  //          .perform(
  //              post("/auth/refresh")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //
  // .content(objectMapper.writeValueAsString(createRefreshTokenRequest(originalRefreshToken))))
  //          .andExpect(status().isUnauthorized());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return error when refresh token is invalid")
  //    void shouldReturnErrorWhenRefreshTokenIsInvalid() throws Exception {
  //      RefreshTokenRequest request = createRefreshTokenRequest("invalid-refresh-token");
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/refresh")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isUnauthorized());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return validation error when refresh token is blank")
  //    void shouldReturnValidationErrorWhenRefreshTokenIsBlank() throws Exception {
  //      RefreshTokenRequest request = createRefreshTokenRequest("");
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/refresh")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isBadRequest());
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("POST /auth/logout")
  //  class LogoutTests {
  //
  //    @Test
  //    @DisplayName("Should logout successfully")
  //    void shouldLogoutSuccessfully() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      String refreshToken = loginAndGetRefreshToken(TEST_USERNAME, TEST_PASSWORD);
  //
  //      RefreshTokenRequest request = createRefreshTokenRequest(refreshToken);
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/logout")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.code", is(1000)))
  //          .andExpect(jsonPath("$.message", is("success")));
  //    }
  //
  //    @Test
  //    @DisplayName("Should invalidate refresh token after logout")
  //    void shouldInvalidateRefreshTokenAfterLogout() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      String refreshToken = loginAndGetRefreshToken(TEST_USERNAME, TEST_PASSWORD);
  //
  //      // Logout
  //      mockMvc
  //          .perform(
  //              post("/auth/logout")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //
  // .content(objectMapper.writeValueAsString(createRefreshTokenRequest(refreshToken))))
  //          .andExpect(status().isOk());
  //
  //      // Try to use the same refresh token - should fail
  //      mockMvc
  //          .perform(
  //              post("/auth/refresh")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //
  // .content(objectMapper.writeValueAsString(createRefreshTokenRequest(refreshToken))))
  //          .andExpect(status().isNotFound());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return error when refresh token is invalid")
  //    void shouldReturnErrorWhenRefreshTokenIsInvalid() throws Exception {
  //      RefreshTokenRequest request = createRefreshTokenRequest("invalid-refresh-token");
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/logout")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isNotFound());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return validation error when refresh token is blank")
  //    void shouldReturnValidationErrorWhenRefreshTokenIsBlank() throws Exception {
  //      RefreshTokenRequest request = createRefreshTokenRequest("");
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/logout")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isBadRequest());
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("GET /auth/me")
  //  class GetCurrentUserTests {
  //
  //    @Test
  //    @DisplayName("Should return current user when authenticated")
  //    void shouldReturnCurrentUserWhenAuthenticated() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      String accessToken = loginAndGetAccessToken(TEST_USERNAME, TEST_PASSWORD);
  //
  //      mockMvc
  //          .perform(
  //              get("/auth/me")
  //                  .header("Authorization", "Bearer " + accessToken)
  //                  .contentType(MediaType.APPLICATION_JSON))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.code", is(1000)))
  //          .andExpect(jsonPath("$.message", is("success")))
  //          .andExpect(jsonPath("$.data.username", is(TEST_USERNAME)))
  //          .andExpect(jsonPath("$.data.email", is(TEST_EMAIL)))
  //          .andExpect(jsonPath("$.data.phone", is(TEST_PHONE)))
  //          .andExpect(jsonPath("$.data.role", is("PASSENGER")));
  //    }
  //
  //    @Test
  //    @DisplayName("Should return current user with admin role")
  //    void shouldReturnCurrentUserWithAdminRole() throws Exception {
  //      createAndSaveUser("admin", "admin@example.com", "0111111111", TEST_PASSWORD,
  // RoleEnum.ADMIN);
  //      String accessToken = loginAndGetAccessToken("admin", TEST_PASSWORD);
  //
  //      mockMvc
  //          .perform(
  //              get("/auth/me")
  //                  .header("Authorization", "Bearer " + accessToken)
  //                  .contentType(MediaType.APPLICATION_JSON))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.data.username", is("admin")))
  //          .andExpect(jsonPath("$.data.role", is("ADMIN")));
  //    }
  //
  //    @Test
  //    @DisplayName("Should return error when not authenticated")
  //    void shouldReturnErrorWhenNotAuthenticated() throws Exception {
  //      mockMvc
  //          .perform(
  //              get("/auth/me")
  //                  .contentType(MediaType.APPLICATION_JSON))
  //          .andExpect(status().isUnauthorized());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return error when token is invalid")
  //    void shouldReturnErrorWhenTokenIsInvalid() throws Exception {
  //      mockMvc
  //          .perform(
  //              get("/auth/me")
  //                  .header("Authorization", "Bearer invalid-token")
  //                  .contentType(MediaType.APPLICATION_JSON))
  //          .andExpect(status().isUnauthorized());
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("POST /auth/change-password")
  //  class ChangePasswordTests {
  //
  //    @Test
  //    @DisplayName("Should change password successfully")
  //    void shouldChangePasswordSuccessfully() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      String accessToken = loginAndGetAccessToken(TEST_USERNAME, TEST_PASSWORD);
  //
  //      ChangePasswordRequest request = createChangePasswordRequest(TEST_PASSWORD,
  // "newpassword123");
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/change-password")
  //                  .header("Authorization", "Bearer " + accessToken)
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.code", is(1000)))
  //          .andExpect(jsonPath("$.message", is("success")));
  //    }
  //
  //    @Test
  //    @DisplayName("Should allow login with new password after change")
  //    void shouldAllowLoginWithNewPasswordAfterChange() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      String accessToken = loginAndGetAccessToken(TEST_USERNAME, TEST_PASSWORD);
  //
  //      String newPassword = "newpassword123";
  //      ChangePasswordRequest request = createChangePasswordRequest(TEST_PASSWORD, newPassword);
  //
  //      // Change password
  //      mockMvc
  //          .perform(
  //              post("/auth/change-password")
  //                  .header("Authorization", "Bearer " + accessToken)
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isOk());
  //
  //      // Login with new password
  //      mockMvc
  //          .perform(
  //              post("/auth/login")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(createLoginRequest(TEST_USERNAME,
  // newPassword))))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.data.accessToken", notNullValue()));
  //    }
  //
  //    @Test
  //    @DisplayName("Should reject old password after change")
  //    void shouldRejectOldPasswordAfterChange() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      String accessToken = loginAndGetAccessToken(TEST_USERNAME, TEST_PASSWORD);
  //
  //      String newPassword = "newpassword123";
  //      ChangePasswordRequest request = createChangePasswordRequest(TEST_PASSWORD, newPassword);
  //
  //      // Change password
  //      mockMvc
  //          .perform(
  //              post("/auth/change-password")
  //                  .header("Authorization", "Bearer " + accessToken)
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isOk());
  //
  //      // Try to login with old password
  //      mockMvc
  //          .perform(
  //              post("/auth/login")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(createLoginRequest(TEST_USERNAME,
  // TEST_PASSWORD))))
  //          .andExpect(status().isUnauthorized());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return error when old password is incorrect")
  //    void shouldReturnErrorWhenOldPasswordIsIncorrect() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      String accessToken = loginAndGetAccessToken(TEST_USERNAME, TEST_PASSWORD);
  //
  //      ChangePasswordRequest request = createChangePasswordRequest("wrongpassword",
  // "newpassword123");
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/change-password")
  //                  .header("Authorization", "Bearer " + accessToken)
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isBadRequest());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return error when not authenticated")
  //    void shouldReturnErrorWhenNotAuthenticated() throws Exception {
  //      ChangePasswordRequest request = createChangePasswordRequest(TEST_PASSWORD,
  // "newpassword123");
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/change-password")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isUnauthorized());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return validation error when old password is blank")
  //    void shouldReturnValidationErrorWhenOldPasswordIsBlank() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      String accessToken = loginAndGetAccessToken(TEST_USERNAME, TEST_PASSWORD);
  //
  //      ChangePasswordRequest request = createChangePasswordRequest("", "newpassword123");
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/change-password")
  //                  .header("Authorization", "Bearer " + accessToken)
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isBadRequest());
  //    }
  //
  //    @Test
  //    @DisplayName("Should return validation error when new password is blank")
  //    void shouldReturnValidationErrorWhenNewPasswordIsBlank() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //      String accessToken = loginAndGetAccessToken(TEST_USERNAME, TEST_PASSWORD);
  //
  //      ChangePasswordRequest request = createChangePasswordRequest(TEST_PASSWORD, "");
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/change-password")
  //                  .header("Authorization", "Bearer " + accessToken)
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(request)))
  //          .andExpect(status().isBadRequest());
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("Complete Auth Workflow")
  //  class AuthWorkflowTests {
  //
  //    @Test
  //    @DisplayName("Should perform complete auth workflow: register -> login -> get me -> change
  // password -> logout")
  //    void shouldPerformCompleteAuthWorkflow() throws Exception {
  //      // 1. Register (bypassed - create user directly in DB due to email/phone verification)
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //
  //      // 2. Login
  //      LoginRequest loginRequest = createLoginRequest(TEST_USERNAME, TEST_PASSWORD);
  //      MvcResult loginResult = mockMvc
  //          .perform(
  //              post("/auth/login")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(loginRequest)))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.data.accessToken", notNullValue()))
  //          .andReturn();
  //
  //      String accessToken = objectMapper
  //          .readTree(loginResult.getResponse().getContentAsString())
  //          .path("data")
  //          .path("accessToken")
  //          .asText();
  //      String refreshToken = objectMapper
  //          .readTree(loginResult.getResponse().getContentAsString())
  //          .path("data")
  //          .path("refreshToken")
  //          .asText();
  //
  //      // 3. Get current user
  //      mockMvc
  //          .perform(
  //              get("/auth/me")
  //                  .header("Authorization", "Bearer " + accessToken)
  //                  .contentType(MediaType.APPLICATION_JSON))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.data.username", is(TEST_USERNAME)));
  //
  //      // 4. Refresh token
  //      MvcResult refreshResult = mockMvc
  //          .perform(
  //              post("/auth/refresh")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //
  // .content(objectMapper.writeValueAsString(createRefreshTokenRequest(refreshToken))))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.data.accessToken", notNullValue()))
  //          .andReturn();
  //
  //      String newAccessToken = objectMapper
  //          .readTree(refreshResult.getResponse().getContentAsString())
  //          .path("data")
  //          .path("accessToken")
  //          .asText();
  //      String newRefreshToken = objectMapper
  //          .readTree(refreshResult.getResponse().getContentAsString())
  //          .path("data")
  //          .path("refreshToken")
  //          .asText();
  //
  //      // 5. Change password
  //      mockMvc
  //          .perform(
  //              post("/auth/change-password")
  //                  .header("Authorization", "Bearer " + newAccessToken)
  //                  .contentType(MediaType.APPLICATION_JSON)
  //
  // .content(objectMapper.writeValueAsString(createChangePasswordRequest(TEST_PASSWORD,
  // "newpassword123"))))
  //          .andExpect(status().isOk());
  //
  //      // 6. Logout
  //      mockMvc
  //          .perform(
  //              post("/auth/logout")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //
  // .content(objectMapper.writeValueAsString(createRefreshTokenRequest(newRefreshToken))))
  //          .andExpect(status().isOk());
  //
  //      // 7. Verify can login with new password
  //      mockMvc
  //          .perform(
  //              post("/auth/login")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(objectMapper.writeValueAsString(createLoginRequest(TEST_USERNAME,
  // "newpassword123"))))
  //          .andExpect(status().isOk())
  //          .andExpect(jsonPath("$.data.accessToken", notNullValue()));
  //    }
  //
  //    @Test
  //    @DisplayName("Should handle multiple login sessions")
  //    void shouldHandleMultipleLoginSessions() throws Exception {
  //      createAndSaveUser(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD,
  // RoleEnum.PASSENGER);
  //
  //      // Login first time
  //      String refreshToken1 = loginAndGetRefreshToken(TEST_USERNAME, TEST_PASSWORD);
  //
  //      // Login second time
  //      String refreshToken2 = loginAndGetRefreshToken(TEST_USERNAME, TEST_PASSWORD);
  //
  //      // Both refresh tokens should be valid
  //      mockMvc
  //          .perform(
  //              post("/auth/refresh")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //
  // .content(objectMapper.writeValueAsString(createRefreshTokenRequest(refreshToken1))))
  //          .andExpect(status().isOk());
  //
  //      mockMvc
  //          .perform(
  //              post("/auth/refresh")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //
  // .content(objectMapper.writeValueAsString(createRefreshTokenRequest(refreshToken2))))
  //          .andExpect(status().isOk());
  //    }
  //  }
}
