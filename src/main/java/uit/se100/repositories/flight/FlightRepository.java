package uit.se100.repositories.flight;

import org.springframework.stereotype.Repository;
import uit.se100.entities.flight.Flight;
import uit.se100.repositories.SimpleRepository;

@Repository
public interface FlightRepository extends SimpleRepository<Flight, Long> {}
