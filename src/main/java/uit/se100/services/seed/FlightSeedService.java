package uit.se100.services.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.dtos.flight.FlightRequest;
import uit.se100.dtos.flight.FlightResponse;
import uit.se100.entities.flight.Flight;
import uit.se100.services.CrudService;
import uit.se100.utils.JsonSeedReader;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightSeedService {

    private final JsonSeedReader jsonSeedReader;
    private final CrudService<Flight, Long, FlightRequest, FlightResponse> flightService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void seed() {
        List<FlightRequest> dtos =
                jsonSeedReader.readList("seed/flight.json", FlightRequest.class);

        dtos.forEach(flightService::create);

        log.info("Seeded {} flights", dtos.size());
    }
}

