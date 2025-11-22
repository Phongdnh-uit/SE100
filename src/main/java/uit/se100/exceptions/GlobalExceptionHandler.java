package uit.se100.exceptions;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uit.se100.dtos.ApiResponse;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
    ApiResponse<Void> response = new ApiResponse<>();
    response.setCode(ex.getErrorCode().getCode());
    response.setMessage(ex.getMessage() != null ? ex.getMessage() : ex.getErrorCode().getMessage());
    response.setErrors(ex.getFieldErrors());
    return ResponseEntity.status(ex.getErrorCode().getHttpCode()).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationException(
      MethodArgumentNotValidException ex) {
    ApiResponse<Void> response = new ApiResponse<>();
    Map<String, String> fieldErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField,
                    org.springframework.validation.FieldError::getDefaultMessage,
                    (existing, _) -> existing));
    response.setCode(ErrorCode.VALIDATION_ERROR.getCode());
    response.setMessage(ErrorCode.VALIDATION_ERROR.getMessage());
    response.setErrors(fieldErrors);
    return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getHttpCode()).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleUncatchException(Exception ex) {
    ApiResponse<Void> response = new ApiResponse<>();
    response.setCode(ErrorCode.INTERNAL_SERVER_ERROR.getCode());
    response.setMessage(
        ex.getMessage().isEmpty() ? ErrorCode.INTERNAL_SERVER_ERROR.getMessage() : ex.getMessage());
    return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getHttpCode()).body(response);
  }
}
