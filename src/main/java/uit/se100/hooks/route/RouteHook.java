package uit.se100.hooks.route;

import org.springframework.stereotype.Component;
import uit.se100.dtos.route.RouteRequest;
import uit.se100.dtos.route.RouteResponse;
import uit.se100.entities.route.Route;
import uit.se100.hooks.GenericHook;

@Component
public class RouteHook implements GenericHook<Route, Long, RouteRequest, RouteResponse> {}
