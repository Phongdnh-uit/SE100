package uit.se100.repositories.flight;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uit.se100.entities.flight.Flight;
import uit.se100.enums.flight.FlightStatus;
import uit.se100.repositories.SimpleRepository;

@Repository
public interface FlightRepository extends SimpleRepository<Flight, Long> {

  // Find flights that are ready to depart (departure time has passed and status is OPEN, FULL, or DELAYED)
  @Query("SELECT f FROM Flight f WHERE f.departureTime <= :currentTime AND f.status IN :statuses")
  List<Flight> findFlightsReadyToDepart(
      @Param("currentTime") Instant currentTime,
      @Param("statuses") List<FlightStatus> statuses);

  // Find flights that are ready to complete (arrival time has passed and status is DEPARTED)
  @Query("SELECT f FROM Flight f WHERE f.arrivalTime <= :currentTime AND f.status = :status")
  List<Flight> findFlightsReadyToComplete(
      @Param("currentTime") Instant currentTime,
      @Param("status") FlightStatus status);
}
