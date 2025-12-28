package uit.se100.services.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.dtos.aircraft.AircraftRequest;
import uit.se100.dtos.aircraft.AircraftResponse;
import uit.se100.entities.aircraft.Aircraft;
import uit.se100.repositories.aircraft.AircraftRepository;
import uit.se100.services.CrudService;
import uit.se100.utils.JsonSeedReader;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AircraftSeedService {

    private final AircraftRepository aircraftRepository;
    private final JsonSeedReader jsonSeedReader;
    private final CrudService<Aircraft, Long, AircraftRequest, AircraftResponse> aircraftService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void seedAircraft() {
        List<AircraftRequest> dtos =
                jsonSeedReader.readList("seed/aircraft.json", AircraftRequest.class);

        dtos.forEach(aircraftService::create);

        log.info("Seeded {} aircraft", dtos.size());
    }
}
