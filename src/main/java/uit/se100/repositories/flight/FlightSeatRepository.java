package uit.se100.repositories.flight;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uit.se100.entities.flight.FlightSeat;
import uit.se100.enums.seat.SeatClass;
import uit.se100.repositories.SimpleRepository;

import java.util.Optional;

public interface FlightSeatRepository extends SimpleRepository<FlightSeat, Long> {
    @Query("""
            SELECT s
            FROM FlightSeat s
            WHERE s.flight.id = :flightId
              AND s.seatClass = :seatClass
              AND s.status = 'AVAILABLE'
            ORDER BY s.id
            """)
    Optional<FlightSeat> findAvailableSeat(
            @Param("flightId") Long flightId,
            @Param("seatClass") SeatClass seatClass
    );
}
