package uit.se100.repositories.seat;

import org.springframework.stereotype.Repository;
import uit.se100.entities.seat.Seat;
import uit.se100.repositories.SimpleRepository;

@Repository
public interface SeatRepository extends SimpleRepository<Seat, Long> {}
