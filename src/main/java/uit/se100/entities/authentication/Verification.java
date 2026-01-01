package uit.se100.entities.authentication;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.enums.authentication.VerificationType;

@Getter
@Setter
@Entity
@Table(name = "verifications")
public class Verification extends BaseEntity {
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private VerificationType type;

  @Column(unique = true, nullable = false)
  private String code;

  @Column(nullable = false)
  private Instant expiresAt;

  @Column(nullable = false)
  private Long userId;
}
