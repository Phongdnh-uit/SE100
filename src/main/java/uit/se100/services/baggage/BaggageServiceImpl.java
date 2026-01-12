package uit.se100.services.baggage;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.dtos.PageResponse;
import uit.se100.dtos.baggage.BaggageRequest;
import uit.se100.dtos.baggage.BaggageResponse;
import uit.se100.entities.baggage.Baggage;
import uit.se100.entities.flight.Flight;
import uit.se100.entities.passenger.Passenger;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.mappers.baggage.BaggageMapper;
import uit.se100.repositories.baggage.BaggageRepository;
import uit.se100.repositories.flight.FlightRepository;
import uit.se100.repositories.passenger.PassengerRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Implementation of BaggageService.
 *
 * <p>Handles baggage management and fee calculation based on passenger tier and baggage type.
 */
@Service
@RequiredArgsConstructor
public class BaggageServiceImpl implements BaggageService {

    // Fee constants
    private static final BigDecimal EXTRA_FEE_PER_5KG = BigDecimal.valueOf(100000L);
    private static final BigDecimal CARRY_ON_FREE_WEIGHT = BigDecimal.valueOf(7);
    private static final BigDecimal ECONOMY_FREE_WEIGHT = BigDecimal.valueOf(20);
    private static final BigDecimal BUSINESS_FREE_WEIGHT = BigDecimal.valueOf(30);
    private static final BigDecimal FIRST_CLASS_FREE_WEIGHT = BigDecimal.valueOf(40);
    private static final BigDecimal ROUNDING_UNIT = BigDecimal.valueOf(5);
    private final BaggageRepository baggageRepository;
    private final PassengerRepository passengerRepository;
    private final FlightRepository flightRepository;
    private final BaggageMapper baggageMapper;

    @Override
    @Transactional(readOnly = true)
    public BaggageResponse findById(Long id) {
        Baggage baggage =
                baggageRepository
                        .findById(id)
                        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Baggage not found"));
        return baggageMapper.entityToResponse(baggage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BaggageResponse> findByPassengerId(Long passengerId, Pageable pageable) {
        if (passengerRepository.findById(passengerId).isEmpty()) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Passenger not found");
        }

        List<BaggageResponse> content =
                baggageRepository.findByPassengerId(passengerId).stream()
                        .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                        .limit(pageable.getPageSize())
                        .map(baggageMapper::entityToResponse)
                        .toList();

        long total = baggageRepository.findByPassengerId(passengerId).size();
        int totalPages = (int) Math.ceil((double) total / pageable.getPageSize());

        return new PageResponse<>(pageable.getPageNumber(), pageable.getPageSize(), total, totalPages, content.size(), content);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BaggageResponse> findByFlightId(Long flightId, Pageable pageable) {
        if (flightRepository.findById(flightId).isEmpty()) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Flight not found");
        }

        List<BaggageResponse> content =
                baggageRepository.findByFlightId(flightId).stream()
                        .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                        .limit(pageable.getPageSize())
                        .map(baggageMapper::entityToResponse)
                        .toList();

        long total = baggageRepository.findByFlightId(flightId).size();
        int totalPages = (int) Math.ceil((double) total / pageable.getPageSize());

        return new PageResponse<>(pageable.getPageNumber(), pageable.getPageSize(), total, totalPages, content.size(), content);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateExtraFee(BaggageRequest request) {
        // Fetch passenger to get tier information
        Passenger passenger =
                passengerRepository
                        .findById(request.getPassengerId())
                        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Passenger not found"));

        BigDecimal weight = request.getWeight();

        // Determine free weight based on baggage type and passenger tier
        BigDecimal freeWeight = switch (request.getType()) {
            case CARRY_ON -> CARRY_ON_FREE_WEIGHT;
            case CHECKED -> switch (passenger.getTier()) {
                case BUSINESS -> BUSINESS_FREE_WEIGHT;
                case FIRST -> FIRST_CLASS_FREE_WEIGHT;
                case ECONOMY -> ECONOMY_FREE_WEIGHT;
            };
        };

        // Calculate excess weight
        BigDecimal excessWeight = weight.subtract(freeWeight);

        // If no excess, no fee
        if (excessWeight.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Round up to nearest 5kg
        BigDecimal units = excessWeight.divide(ROUNDING_UNIT, RoundingMode.UP);
        BigDecimal roundedExcessWeight = units.multiply(ROUNDING_UNIT);

        // Calculate fee: (rounded excess weight / 5) * 100,000
        BigDecimal feeUnits = roundedExcessWeight.divide(ROUNDING_UNIT, RoundingMode.HALF_UP);
        return feeUnits.multiply(EXTRA_FEE_PER_5KG);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalWeightForPassengerOnFlight(Long passengerId, Long flightId) {
        List<Baggage> baggageList =
                baggageRepository.findByPassengerIdAndFlightId(passengerId, flightId);

        return baggageList.stream()
                .map(Baggage::getWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Create baggage with automatic fee calculation.
     *
     * @param request the baggage request
     * @return the created baggage response
     */
    @Transactional
    public BaggageResponse create(BaggageRequest request) {
        Passenger passenger =
                passengerRepository
                        .findById(request.getPassengerId())
                        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Passenger not found"));

        Flight flight =
                flightRepository
                        .findById(request.getFlightId())
                        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Flight not found"));

        Baggage baggage = new Baggage();
        baggage.setType(request.getType());
        baggage.setWeight(request.getWeight());
        baggage.setPassenger(passenger);
        baggage.setFlight(flight);

        // Calculate extra fee
        BigDecimal extraFee = calculateExtraFee(request);
        baggage.setExtraFee(extraFee);

        Baggage savedBaggage = baggageRepository.save(baggage);
        return baggageMapper.entityToResponse(savedBaggage);
    }

    /**
     * Update baggage with automatic fee recalculation.
     *
     * @param id      the baggage ID
     * @param request the updated baggage request
     * @return the updated baggage response
     */
    @Transactional
    public BaggageResponse update(Long id, BaggageRequest request) {
        Baggage baggage =
                baggageRepository
                        .findById(id)
                        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Baggage not found"));

        Passenger passenger =
                passengerRepository
                        .findById(request.getPassengerId())
                        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Passenger not found"));

        baggage.setType(request.getType());
        baggage.setWeight(request.getWeight());
        baggage.setPassenger(passenger);

        // Recalculate extra fee
        BigDecimal extraFee = calculateExtraFee(request);
        baggage.setExtraFee(extraFee);

        Baggage updatedBaggage = baggageRepository.save(baggage);
        return baggageMapper.entityToResponse(updatedBaggage);
    }

    /**
     * Delete baggage record.
     *
     * @param id the baggage ID
     */
    @Transactional
    public void delete(Long id) {
        baggageRepository
                .findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Baggage not found"));
        baggageRepository.deleteById(id);
    }
}

