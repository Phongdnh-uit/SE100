package uit.se100.services.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.dtos.route.RouteRequest;
import uit.se100.dtos.route.RouteResponse;
import uit.se100.entities.route.Route;
import uit.se100.services.CrudService;
import uit.se100.utils.JsonSeedReader;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteSeedService {

    private final JsonSeedReader jsonSeedReader;
    private final CrudService<Route, Long, RouteRequest, RouteResponse> routeService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void seed() {
        List<RouteRequest> dtos =
                jsonSeedReader.readList("seed/route.json", RouteRequest.class);

        dtos.forEach(routeService::create);

        log.info("Seeded {} routes", dtos.size());
    }
}
