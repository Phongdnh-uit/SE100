package uit.se100.entities.route;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "routes")
public class Route extends BaseEntity {
  @Column(nullable = false)
  private String origin;

  @Column(nullable = false)
  private String destination;
}
