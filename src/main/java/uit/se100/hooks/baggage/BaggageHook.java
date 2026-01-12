package uit.se100.hooks.baggage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uit.se100.dtos.baggage.BaggageRequest;
import uit.se100.dtos.baggage.BaggageResponse;
import uit.se100.entities.baggage.Baggage;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.hooks.GenericHook;
import uit.se100.repositories.baggage.BaggageRepository;
import uit.se100.repositories.flight.FlightRepository;
import uit.se100.repositories.passenger.PassengerRepository;

import java.util.Map;

/**
 * Hook for Baggage entity validation and enrichment.
 *
 * <p>Handles:
 * <ul>
 *   <li>Validation of passenger and flight existence
 *   <li>Weight validation
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class BaggageHook implements GenericHook<Baggage, Long, BaggageRequest, BaggageResponse> {

    private final PassengerRepository passengerRepository;
    private final FlightRepository flightRepository;
    private final BaggageRepository baggageRepository;

    @Override
    public void validateCreate(BaggageRequest input, Map<String, Object> context) {
        validateRequest(input);
    }

    @Override
    public void validateUpdate(
            Long id, BaggageRequest input, Baggage existingEntity, Map<String, Object> context) {
        validateRequest(input);
    }

    @Override
    public void validateDelete(Long id) {
        baggageRepository
                .findById(id)
                .orElseThrow(
                        () -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Baggage not found"));
    }

    /**
     * Validate baggage request.
     *
     * @param input the baggage request
     */
    private void validateRequest(BaggageRequest input) {
        // Check if passenger exists
        if (!passengerRepository.existsById(input.getPassengerId())) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Passenger not found");
        }

        // Check if flight exists
        if (!flightRepository.existsById(input.getFlightId())) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Flight not found");
        }

        // Validate weight (must be positive)
        if (input.getWeight() == null || input.getWeight().signum() <= 0) {
            throw new ApiException(ErrorCode.DATA_INTEGRITY_VIOLATION, "Weight must be greater than 0");
        }
    }
}

