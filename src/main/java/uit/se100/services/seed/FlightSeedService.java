package uit.se100.services.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.dtos.flight.FlightRequest;
import uit.se100.dtos.flight.FlightResponse;
import uit.se100.entities.aircraft.Aircraft;
import uit.se100.entities.flight.Flight;
import uit.se100.entities.route.Route;
import uit.se100.repositories.aircraft.AircraftRepository;
import uit.se100.repositories.route.RouteRepository;
import uit.se100.services.CrudService;
import uit.se100.utils.JsonSeedReader;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightSeedService {

    private final JsonSeedReader jsonSeedReader;
    private final CrudService<Flight, Long, FlightRequest, FlightResponse> flightService;
    private final RouteRepository routeRepository;
    private final AircraftRepository aircraftRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void seed() {
        List<Route> routes = routeRepository.findAll();
        final List<FlightRequest>[] dtos = new List[]{jsonSeedReader.readList("seed/flight.json", FlightRequest.class)};
        List<Aircraft> aircrafts = aircraftRepository.findAll();

        final int[] x = {0};

        routes.forEach(route -> {
            dtos[0] = dtos[0].stream().map(dto -> {
                dto.setRouteId(route.getId());
                dto.setAircraftId(aircrafts.get(x[0]).getId());
                x[0] = (x[0]++) / 5;
                return dto;
            }).toList();
        });


        dtos[0].forEach(flightService::create);

        log.info("Seeded {} flights", dtos[0].size());
    }
}

