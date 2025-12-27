package uit.se100.dtos.route;

import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;

@Getter
@Setter
public class RouteResponse extends BaseEntity {
  private String origin;
  private String destination;
}
