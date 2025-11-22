package uit.se100.dtos;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
  private Integer code;
  private String message;
  private T data;
  private Map<String, String> errors;
  private Instant timestamp = Instant.now();

  public static <T> ApiResponse<T> ok(T data) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setCode(1000);
    response.setMessage("success");
    response.setData(data);
    return response;
  }

  public static <T> ApiResponse<T> ok(String customMessage, T data) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setCode(1000);
    response.setMessage(customMessage);
    response.setData(data);
    return response;
  }
}
