package uit.se100.entities.authentication;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.entities.employee.Employee;
import uit.se100.entities.passenger.Passenger;
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

  @OneToMany(
      mappedBy = "user",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = jakarta.persistence.FetchType.LAZY)
  private List<RefreshToken> refreshTokens = new ArrayList<>();

  @OneToOne(mappedBy = "user")
  private Passenger passenger;

  @OneToOne(mappedBy = "user")
  private Employee employee;
}
