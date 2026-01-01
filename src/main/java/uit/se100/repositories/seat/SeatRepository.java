package uit.se100.repositories.seat;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uit.se100.entities.seat.Seat;
import uit.se100.repositories.SimpleRepository;

@Repository
public interface SeatRepository extends SimpleRepository<Seat, Long> {

  @EntityGraph(attributePaths = {"aircraft"})
  @Override
  <S extends Seat> List<S> saveAll(Iterable<S> entities);
  // @Query("""
  //         SELECT s
  //         FROM Seat s
  //         WHERE s.flight.id = :flightId
  //           AND s.seatClass = :seatClass
  //           AND s.status = 'AVAILABLE'
  //         ORDER BY s.id
  //         """)
  // Optional<Seat> findAvailableSeat(
  //         @Param("flightId") Long flightId,
  //         @Param("seatClass") SeatClass seatClass
  // );
  //

}
