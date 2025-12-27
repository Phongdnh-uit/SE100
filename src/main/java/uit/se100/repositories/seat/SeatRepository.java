package uit.se100.repositories.seat;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uit.se100.entities.seat.Seat;
import uit.se100.enums.seat.SeatClass;
import uit.se100.repositories.SimpleRepository;

import java.util.Optional;

@Repository
public interface SeatRepository extends SimpleRepository<Seat, Long> {
    @Query("""
            SELECT s
            FROM Seat s
            WHERE s.flight.id = :flightId
              AND s.seatClass = :seatClass
              AND s.status = 'AVAILABLE'
            ORDER BY s.id
            """)
    Optional<Seat> findAvailableSeat(
            @Param("flightId") Long flightId,
            @Param("seatClass") SeatClass seatClass
    );
}
