package uit.se100.entities.authentication;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.enums.RoleEnum;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {
  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String passwordHash;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(unique = true, nullable = false)
  private String phone;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private RoleEnum role;
}
