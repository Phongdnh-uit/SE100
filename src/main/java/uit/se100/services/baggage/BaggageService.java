package uit.se100.services.baggage;

import org.springframework.data.domain.Pageable;
import uit.se100.dtos.PageResponse;
import uit.se100.dtos.baggage.BaggageRequest;
import uit.se100.dtos.baggage.BaggageResponse;

/**
 * Service for managing baggage operations.
 *
 * <p>Provides functionality for:
 * <ul>
 *   <li>Creating and managing baggage records
 *   <li>Calculating extra fees based on baggage weight and type
 *   <li>Retrieving baggage information for passengers and flights
 * </ul>
 */
public interface BaggageService {

    /**
     * Find baggage by ID.
     *
     * @param id the baggage ID
     * @return baggage response
     */
    BaggageResponse findById(Long id);

    /**
     * Find all baggage for a specific passenger.
     *
     * @param passengerId the passenger ID
     * @param pageable    pagination information
     * @return page of baggage for the passenger
     */
    PageResponse<BaggageResponse> findByPassengerId(Long passengerId, Pageable pageable);

    /**
     * Find all baggage for a specific flight.
     *
     * @param flightId the flight ID
     * @param pageable pagination information
     * @return page of baggage for the flight
     */
    PageResponse<BaggageResponse> findByFlightId(Long flightId, Pageable pageable);

    /**
     * Calculate the extra fee for baggage based on weight and type.
     *
     * <p>Fee calculation rules:
     * <ul>
     *   <li>Carry-on: 7kg free, then 100,000 VND per 5kg (rounded up)
     *   <li>Checked (Economy): 20kg free, then 100,000 VND per 5kg (rounded up)
     *   <li>Checked (Business): 30kg free, then 100,000 VND per 5kg (rounded up)
     *   <li>Checked (First class): 40kg free, then 100,000 VND per 5kg (rounded up)
     * </ul>
     *
     * @param request the baggage request containing type, weight, and passenger tier
     * @return the calculated extra fee in VND
     */
    java.math.BigDecimal calculateExtraFee(BaggageRequest request);

    /**
     * Get total weight of baggage for a passenger on a flight.
     *
     * @param passengerId the passenger ID
     * @param flightId    the flight ID
     * @return total weight of baggage
     */
    java.math.BigDecimal getTotalWeightForPassengerOnFlight(Long passengerId, Long flightId);
}

