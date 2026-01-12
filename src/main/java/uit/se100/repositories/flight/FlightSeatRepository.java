package uit.se100.repositories.flight;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uit.se100.entities.flight.FlightSeat;
import uit.se100.enums.seat.SeatClass;
import uit.se100.enums.seat.SeatStatus;
import uit.se100.projections.SeatAvailableProjection;
import uit.se100.repositories.SimpleRepository;

import java.util.List;
import java.util.Optional;

public interface FlightSeatRepository extends SimpleRepository<FlightSeat, Long> {
//    @Query("""
//            SELECT s
//            FROM FlightSeat s
//            WHERE s.flight.id = :flightId
//              AND s.seatClass = :seatClass
//              AND s.status = 'AVAILABLE'
//            ORDER BY s.id
//            """)
//    Optional<FlightSeat> findAvailableSeat(
//            @Param("flightId") Long flightId,
//            @Param("seatClass") SeatClass seatClass
//    );

    @Query("""
                SELECT 
                    fs.seatClass AS seatClass,
                    COUNT(fs.id) AS availableSeats
                FROM FlightSeat fs
                WHERE fs.flight.id = :flightId
                GROUP BY fs.seatClass
            """)
    List<SeatAvailableProjection> countSeatsByClass(
            @Param("flightId") Long flightId
    );

    Optional<FlightSeat> findFirstBySeatClassAndFlightIdOrderByPriceAsc(
            SeatClass seatClass,
            Long flightId
    );

    Page<FlightSeat> findBySeatClassAndFlightIdAndStatusOrderById(SeatClass seatClass, SeatStatus status, Long flightId, Pageable pageable);


}
