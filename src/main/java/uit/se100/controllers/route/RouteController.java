package uit.se100.controllers.route;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uit.se100.controllers.GenericController;
import uit.se100.dtos.route.RouteRequest;
import uit.se100.dtos.route.RouteResponse;
import uit.se100.entities.route.Route;
import uit.se100.services.CrudService;

@Tag(name = "Route")
@RequestMapping("/routes")
@RestController
public class RouteController extends GenericController<Route, Long, RouteRequest, RouteResponse> {

  public RouteController(CrudService<Route, Long, RouteRequest, RouteResponse> service) {
    super(service);
  }
}
