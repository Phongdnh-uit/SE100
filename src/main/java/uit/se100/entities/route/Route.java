package uit.se100.entities.route;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.entities.flight.Flight;

@Getter
@Setter
@Entity
@Table(name = "routes")
public class Route extends BaseEntity {
  @Column(nullable = false)
  private String origin;

  @Column(nullable = false)
  private String destination;

  private boolean isExternal = false;

  @OneToMany(mappedBy = "route")
  private List<Flight> flights = new ArrayList<>();
}
