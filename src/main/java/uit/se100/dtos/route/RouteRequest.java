package uit.se100.dtos.route;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteRequest {
  @NotBlank private String origin;
  @NotBlank private String destination;
  private boolean isExternal = false;
}
