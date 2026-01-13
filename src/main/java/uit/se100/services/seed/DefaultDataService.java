package uit.se100.services.seed;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultDataService {
    @PersistenceContext
    private final EntityManager entityManager;
    private final AircraftSeedService aircraftSeedService;
    private final DatabaseCleanupService databaseCleanupService;
    private final RouteSeedService routeSeedService;
    private final SeatSeedService seatSeedService;
    private final FlightSeedService flightSeedService;
    private final PassengerSeedService passengerSeedService;
    private final EmployeeSeedService employeeSeedService;


    public void loadDefaultData() {
        databaseCleanupService.deleteTables();

        aircraftSeedService.seed();
        routeSeedService.seed();
        seatSeedService.seed();
        flightSeedService.seed();
        passengerSeedService.seed();
        employeeSeedService.seed();
    }
}
