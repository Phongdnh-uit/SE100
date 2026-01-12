package uit.se100.repositories.ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uit.se100.entities.ticket.Ticket;
import uit.se100.repositories.SimpleRepository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TicketRepository extends SimpleRepository<Ticket, Long> {
    //Get exist ticket buying by this passenger
    @Query("""
                SELECT COUNT(t)
                FROM Ticket t
                WHERE t.passenger.id = :passengerId
                  AND t.flight.id = :flightId
                  AND t.status IN ('RESERVED', 'PAID', 'WAITING')
            """)
    int countActiveTickets(
            @Param("passengerId") Long passengerId,
            @Param("flightId") Long flightId
    );


//    Optional<Ticket> findFirstByFlightIdAndStatusOrderByCreatedAtAsc(
//            Long flightId,
//            TicketStatus status
//    );

    @Query("""
                SELECT t
                FROM Ticket t
                WHERE t.status = 'RESERVED'
                  AND t.bookedAt < :expiredTime
            """)
    List<Ticket> findExpiredReservations(
            @Param("expiredTime") Instant expiredTime
    );

//    int countByFlightIdAndStatus(Long flightId, TicketStatus status);
//
//    Optional<Ticket> findBySeatIdAndStatusIn(
//            Long seatId,
//            List<TicketStatus> statuses
//    );

    Page<Ticket> findByPassengerId(Long passengerId, Pageable pageable);

}
