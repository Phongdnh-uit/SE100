package uit.se100.services.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.dtos.passenger.PassengerRequest;
import uit.se100.dtos.passenger.PassengerResponse;
import uit.se100.entities.passenger.Passenger;
import uit.se100.services.CrudService;
import uit.se100.utils.JsonSeedReader;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerSeedService {

    private final JsonSeedReader jsonSeedReader;
    private final CrudService<Passenger, Long, PassengerRequest, PassengerResponse> passengerService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void seed() {
        List<PassengerRequest> dtos =
                jsonSeedReader.readList("seed/passenger.json", PassengerRequest.class);

        dtos.forEach(passengerService::create);

        log.info("Seeded {} passenger", dtos.size());
    }
}
