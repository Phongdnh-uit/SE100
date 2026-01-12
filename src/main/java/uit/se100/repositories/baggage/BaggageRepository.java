package uit.se100.repositories.baggage;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uit.se100.entities.baggage.Baggage;
import uit.se100.repositories.SimpleRepository;

import java.util.List;

/**
 * Repository for Baggage entity.
 *
 * <p>Provides custom queries for baggage-related operations.
 */
public interface BaggageRepository extends SimpleRepository<Baggage, Long> {

    /**
     * Find all baggage for a specific passenger.
     *
     * @param passengerId the passenger ID
     * @return list of baggage for the passenger
     */
    List<Baggage> findByPassengerId(Long passengerId);

    /**
     * Find all baggage for a specific flight.
     *
     * @param flightId the flight ID
     * @return list of baggage for the flight
     */
    List<Baggage> findByFlightId(Long flightId);

    /**
     * Find all baggage for a specific passenger on a specific flight.
     *
     * @param passengerId the passenger ID
     * @param flightId    the flight ID
     * @return list of baggage for the passenger on the flight
     */
    @Query(
            "SELECT b FROM Baggage b WHERE b.passenger.id = :passengerId AND b.flight.id = :flightId")
    List<Baggage> findByPassengerIdAndFlightId(
            @Param("passengerId") Long passengerId, @Param("flightId") Long flightId);
}

