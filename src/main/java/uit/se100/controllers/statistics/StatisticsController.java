package uit.se100.controllers.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uit.se100.dtos.ApiResponse;
import uit.se100.dtos.statistics.*;
import uit.se100.services.statistics.StatisticsService;

import java.time.Instant;
import java.util.List;

@Tag(name = "Statistics")
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    // ==================== FLIGHT REPORTS ====================

    /**
     * Report 1: Flights with available seats
     * 
     * Returns all flights that have at least one seat available for booking.
     * Includes flight details and count of available seats.
     */
    @Operation(
            summary = "Get flights with available seats",
            description = "Returns all flights that have at least one available seat for booking"
    )
    @GetMapping("/flights/available-seats")
    public ResponseEntity<ApiResponse<List<FlightAvailableSeatsDTO>>> getFlightsWithAvailableSeats() {
        List<FlightAvailableSeatsDTO> result = statisticsService.getFlightsWithAvailableSeats();
        return ResponseEntity.ok(ApiResponse.ok("Flights with available seats retrieved successfully", result));
    }

    /**
     * Report 2: Passengers per flight
     * 
     * Returns the count of passengers (tickets with PAID/RESERVED status) for each flight.
     */
    @Operation(
            summary = "Get passengers per flight",
            description = "Returns the number of passengers (booked tickets) for each flight"
    )
    @GetMapping("/flights/passengers")
    public ResponseEntity<ApiResponse<List<PassengersPerFlightDTO>>> getPassengersPerFlight() {
        List<PassengersPerFlightDTO> result = statisticsService.getPassengersPerFlight();
        return ResponseEntity.ok(ApiResponse.ok("Passengers per flight retrieved successfully", result));
    }

    /**
     * Report 3: Seat load factor per flight
     * 
     * Returns the seat load factor (sold seats / total capacity) for each flight.
     * Load factor is expressed as a percentage.
     */
    @Operation(
            summary = "Get seat load factor per flight",
            description = "Returns the seat load factor (sold seats / capacity) as percentage for each flight"
    )
    @GetMapping("/flights/load-factor")
    public ResponseEntity<ApiResponse<List<SeatLoadFactorDTO>>> getSeatLoadFactorPerFlight() {
        List<SeatLoadFactorDTO> result = statisticsService.getSeatLoadFactorPerFlight();
        return ResponseEntity.ok(ApiResponse.ok("Seat load factor per flight retrieved successfully", result));
    }

    // ==================== TICKET & REVENUE REPORTS ====================

    /**
     * Report 4: Tickets refunded / changed / canceled
     * 
     * Returns a summary of tickets by problematic statuses.
     */
    @Operation(
            summary = "Get ticket status summary",
            description = "Returns counts of refunded, changed, and canceled tickets"
    )
    @GetMapping("/tickets/status-summary")
    public ResponseEntity<ApiResponse<TicketStatusSummaryDTO>> getTicketStatusSummary() {
        TicketStatusSummaryDTO result = statisticsService.getTicketStatusSummary();
        return ResponseEntity.ok(ApiResponse.ok("Ticket status summary retrieved successfully", result));
    }

    /**
     * Report 5a: Revenue by flight
     * 
     * Returns total revenue (sum of paid ticket prices) for each flight.
     */
    @Operation(
            summary = "Get revenue by flight",
            description = "Returns total revenue from paid tickets grouped by flight"
    )
    @GetMapping("/revenue/by-flight")
    public ResponseEntity<ApiResponse<List<RevenueByFlightDTO>>> getRevenueByFlight() {
        List<RevenueByFlightDTO> result = statisticsService.getRevenueByFlight();
        return ResponseEntity.ok(ApiResponse.ok("Revenue by flight retrieved successfully", result));
    }

    /**
     * Report 5b: Revenue by route
     * 
     * Returns total revenue (sum of paid ticket prices) for each route.
     */
    @Operation(
            summary = "Get revenue by route",
            description = "Returns total revenue from paid tickets grouped by route"
    )
    @GetMapping("/revenue/by-route")
    public ResponseEntity<ApiResponse<List<RevenueByRouteDTO>>> getRevenueByRoute() {
        List<RevenueByRouteDTO> result = statisticsService.getRevenueByRoute();
        return ResponseEntity.ok(ApiResponse.ok("Revenue by route retrieved successfully", result));
    }

    /**
     * Report 5c: Revenue by time range
     * 
     * Returns total revenue for tickets paid within the specified date range.
     * 
     * @param fromDate Start of time range (ISO-8601 format, e.g., 2024-01-01T00:00:00Z)
     * @param toDate End of time range (ISO-8601 format, e.g., 2024-12-31T23:59:59Z)
     */
    @Operation(
            summary = "Get revenue by time range",
            description = "Returns total revenue from paid tickets within the specified date range"
    )
    @GetMapping("/revenue/by-time-range")
    public ResponseEntity<ApiResponse<RevenueByTimeRangeDTO>> getRevenueByTimeRange(
            @Parameter(description = "Start date (ISO-8601 format)", example = "2024-01-01T00:00:00Z")
            @RequestParam Instant fromDate,
            @Parameter(description = "End date (ISO-8601 format)", example = "2024-12-31T23:59:59Z")
            @RequestParam Instant toDate) {
        RevenueByTimeRangeDTO result = statisticsService.getRevenueByTimeRange(fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.ok("Revenue by time range retrieved successfully", result));
    }

    // ==================== BAGGAGE REPORTS ====================

    /**
     * Report 6: Baggage statistics
     * 
     * Returns aggregated baggage statistics including:
     * - Total count
     * - Total weight
     * - Overweight count (checked > 23kg, carry-on > 7kg)
     * - Total extra fees
     */
    @Operation(
            summary = "Get baggage statistics",
            description = "Returns aggregated baggage data: total count, weight, overweight count, and extra fees"
    )
    @GetMapping("/baggage")
    public ResponseEntity<ApiResponse<BaggageStatisticsDTO>> getBaggageStatistics() {
        BaggageStatisticsDTO result = statisticsService.getBaggageStatistics();
        return ResponseEntity.ok(ApiResponse.ok("Baggage statistics retrieved successfully", result));
    }

    // ==================== OPERATIONS REPORTS ====================

    /**
     * Report 7: Aircraft status statistics
     * 
     * Returns count of aircraft by status (ACTIVE, MAINTENANCE, INACTIVE).
     */
    @Operation(
            summary = "Get aircraft status statistics",
            description = "Returns count of aircraft grouped by status"
    )
    @GetMapping("/aircraft/status")
    public ResponseEntity<ApiResponse<AircraftStatusStatisticsDTO>> getAircraftStatusStatistics() {
        AircraftStatusStatisticsDTO result = statisticsService.getAircraftStatusStatistics();
        return ResponseEntity.ok(ApiResponse.ok("Aircraft status statistics retrieved successfully", result));
    }

    /**
     * Report 8a: Crew per flight
     * 
     * Returns crew member counts per flight with breakdown by position.
     */
    @Operation(
            summary = "Get crew per flight",
            description = "Returns crew member counts per flight with breakdown by position (pilot, copilot, attendant)"
    )
    @GetMapping("/crew/by-flight")
    public ResponseEntity<ApiResponse<List<CrewPerFlightDTO>>> getCrewPerFlight() {
        List<CrewPerFlightDTO> result = statisticsService.getCrewPerFlight();
        return ResponseEntity.ok(ApiResponse.ok("Crew per flight retrieved successfully", result));
    }

    /**
     * Report 8b: Employee flight hours
     * 
     * Returns flight hours statistics for each employee.
     * Includes total hours, max allowed hours, and flights assigned.
     */
    @Operation(
            summary = "Get employee flight hours",
            description = "Returns flight hours statistics for each employee including total hours and flights assigned"
    )
    @GetMapping("/crew/flight-hours")
    public ResponseEntity<ApiResponse<List<EmployeeFlightHoursDTO>>> getEmployeeFlightHours() {
        List<EmployeeFlightHoursDTO> result = statisticsService.getEmployeeFlightHours();
        return ResponseEntity.ok(ApiResponse.ok("Employee flight hours retrieved successfully", result));
    }
}

