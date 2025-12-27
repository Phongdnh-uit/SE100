package uit.se100.repositories.passenger;

import org.springframework.stereotype.Repository;
import uit.se100.entities.passenger.Passenger;
import uit.se100.repositories.SimpleRepository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends SimpleRepository<Passenger, Long> {
    Optional<Passenger> findByUserId(Long userId);
}
