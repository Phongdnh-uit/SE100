package uit.se100.repositories.route;

import org.springframework.stereotype.Repository;
import uit.se100.entities.route.Route;
import uit.se100.repositories.SimpleRepository;

@Repository
public interface RouteRepository extends SimpleRepository<Route, Long> {}
