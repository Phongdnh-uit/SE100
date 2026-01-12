package uit.se100.controllers.baggage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uit.se100.dtos.ApiResponse;
import uit.se100.dtos.PageResponse;
import uit.se100.dtos.baggage.BaggageRequest;
import uit.se100.dtos.baggage.BaggageResponse;
import uit.se100.services.baggage.BaggageServiceImpl;

import java.math.BigDecimal;

/**
 * Controller for baggage management.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>POST /baggages - Create new baggage
 *   <li>GET /baggages/{id} - Get baggage by ID
 *   <li>PUT /baggages/{id} - Update baggage
 *   <li>DELETE /baggages/{id} - Delete baggage
 *   <li>GET /baggages/passenger/{passengerId} - Get all baggage for a passenger
 *   <li>GET /baggages/flight/{flightId} - Get all baggage for a flight
 *   <li>POST /baggages/calculate-fee - Calculate baggage fee
 *   <li>GET /baggages/passenger/{passengerId}/flight/{flightId}/total-weight - Get total baggage
 *     weight
 * </ul>
 */
@Tag(name = "Baggage", description = "Baggage Management API")
@RestController
@RequestMapping("/baggages")
@RequiredArgsConstructor
public class BaggageController {

    private final BaggageServiceImpl baggageService;

    /**
     * Create new baggage record.
     *
     * @param request the baggage request
     * @return the created baggage response
     */
    @Operation(summary = "Create new baggage", description = "Create a new baggage record with automatic fee calculation")
    @PostMapping
    public ResponseEntity<ApiResponse<BaggageResponse>> create(@RequestBody BaggageRequest request) {
        BaggageResponse response = baggageService.create(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * Get baggage by ID.
     *
     * @param id the baggage ID
     * @return the baggage response
     */
    @Operation(summary = "Get baggage by ID", description = "Get a baggage record by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BaggageResponse>> getById(@PathVariable("id") Long id) {
        BaggageResponse response = baggageService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * Update baggage record.
     *
     * @param id      the baggage ID
     * @param request the updated baggage request
     * @return the updated baggage response
     */
    @Operation(summary = "Update baggage", description = "Update an existing baggage record with automatic fee recalculation")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BaggageResponse>> update(
            @PathVariable("id") Long id, @RequestBody BaggageRequest request) {
        BaggageResponse response = baggageService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * Delete baggage record.
     *
     * @param id the baggage ID
     * @return success response
     */
    @Operation(summary = "Delete baggage", description = "Delete a baggage record")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        baggageService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /**
     * Get all baggage for a specific passenger.
     *
     * @param passengerId the passenger ID
     * @param pageable    pagination information
     * @return page of baggage for the passenger
     */
    @Operation(
            summary = "Get baggage by passenger",
            description = "Get all baggage records for a specific passenger")
    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<ApiResponse<PageResponse<BaggageResponse>>> getByPassengerId(
            @PathVariable("passengerId") Long passengerId, Pageable pageable) {
        PageResponse<BaggageResponse> response = baggageService.findByPassengerId(passengerId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * Get all baggage for a specific flight.
     *
     * @param flightId the flight ID
     * @param pageable pagination information
     * @return page of baggage for the flight
     */
    @Operation(
            summary = "Get baggage by flight",
            description = "Get all baggage records for a specific flight")
    @GetMapping("/flight/{flightId}")
    public ResponseEntity<ApiResponse<PageResponse<BaggageResponse>>> getByFlightId(
            @PathVariable("flightId") Long flightId, Pageable pageable) {
        PageResponse<BaggageResponse> response = baggageService.findByFlightId(flightId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * Calculate baggage extra fee.
     *
     * <p>Fee calculation:
     * <ul>
     *   <li>Carry-on: 7kg free, then 100,000 VND per 5kg (rounded up)
     *   <li>Checked (Economy): 20kg free, then 100,000 VND per 5kg (rounded up)
     *   <li>Checked (Business): 30kg free, then 100,000 VND per 5kg (rounded up)
     *   <li>Checked (First class): 40kg free, then 100,000 VND per 5kg (rounded up)
     * </ul>
     *
     * @param request the baggage request
     * @return the calculated extra fee
     */
    @Operation(
            summary = "Calculate baggage fee",
            description = "Calculate the extra fee for baggage based on weight and type")
    @PostMapping("/calculate-fee")
    public ResponseEntity<ApiResponse<FeeResponse>> calculateFee(@RequestBody BaggageRequest request) {
        BigDecimal fee = baggageService.calculateExtraFee(request);
        return ResponseEntity.ok(ApiResponse.ok(new FeeResponse(fee)));
    }

    /**
     * Get total weight of baggage for a passenger on a flight.
     *
     * @param passengerId the passenger ID
     * @param flightId    the flight ID
     * @return total weight
     */
    @Operation(
            summary = "Get total baggage weight",
            description = "Get the total weight of baggage for a passenger on a specific flight")
    @GetMapping("/passenger/{passengerId}/flight/{flightId}/total-weight")
    public ResponseEntity<ApiResponse<WeightResponse>> getTotalWeight(
            @PathVariable("passengerId") Long passengerId, @PathVariable("flightId") Long flightId) {
        BigDecimal totalWeight =
                baggageService.getTotalWeightForPassengerOnFlight(passengerId, flightId);
        return ResponseEntity.ok(ApiResponse.ok(new WeightResponse(totalWeight)));
    }

    /**
     * DTO for fee response.
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class FeeResponse {
        private BigDecimal fee;
    }

    /**
     * DTO for weight response.
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class WeightResponse {
        private BigDecimal weight;
    }
}

