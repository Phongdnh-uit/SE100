package uit.se100.dtos.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uit.se100.dtos.authentication.RegisterRequest;
import uit.se100.enums.RoleEnum;

@Getter
@Setter
public class UserRequest extends RegisterRequest {
  @NotNull private RoleEnum role;
}
