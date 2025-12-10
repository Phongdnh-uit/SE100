package uit.se100.repositories.aircraft;

import org.springframework.stereotype.Repository;
import uit.se100.entities.aircraft.Aircraft;
import uit.se100.repositories.SimpleRepository;

@Repository
public interface AircraftRepository extends SimpleRepository<Aircraft, Long> {}
