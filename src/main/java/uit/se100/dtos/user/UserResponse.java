package uit.se100.dtos.user;

import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.enums.RoleEnum;

@Getter
@Setter
public class UserResponse extends BaseEntity {
  private String username;

  private String email;

  private String phone;

  private RoleEnum role;
}
