package uit.se100.services.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.dtos.statistics.AircraftStatusStatisticsDTO;
import uit.se100.dtos.statistics.BaggageStatisticsDTO;
import uit.se100.dtos.statistics.CrewPerFlightDTO;
import uit.se100.dtos.statistics.EmployeeFlightHoursDTO;
import uit.se100.dtos.statistics.FlightAvailableSeatsDTO;
import uit.se100.dtos.statistics.PassengersPerFlightDTO;
import uit.se100.dtos.statistics.RevenueByFlightDTO;
import uit.se100.dtos.statistics.RevenueByRouteDTO;
import uit.se100.dtos.statistics.RevenueByTimeRangeDTO;
import uit.se100.dtos.statistics.SeatLoadFactorDTO;
import uit.se100.dtos.statistics.TicketStatusSummaryDTO;
import uit.se100.repositories.statistics.StatisticsRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Statistics module.
 * Contains all business logic for generating statistical reports.
 * All queries are performed in repositories using JPQL.
 * Returns only DTOs, never entities.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    // ==================== REPORT 1: Flights with available seats ====================
    /**
     * Business Logic:
     * - Retrieves all flights that have at least one seat with status 'AVAILABLE'
     * - Returns flight details including origin, destination, times, and available seat count
     * - Useful for passengers searching for bookable flights
     * 
     * Tables Involved: flight, flight_seat, aircraft, route
     * 
     * @return List of FlightAvailableSeatsDTO with flights having available seats
     */
    public List<FlightAvailableSeatsDTO> getFlightsWithAvailableSeats() {
        List<Object[]> results = statisticsRepository.findFlightsWithAvailableSeats();
        
        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }

        return results.stream()
                .map(row -> FlightAvailableSeatsDTO.builder()
                        .flightId(((Number) row[0]).longValue())
                        .origin((String) row[1])
                        .destination((String) row[2])
                        .departureTime((Instant) row[3])
                        .arrivalTime((Instant) row[4])
                        .aircraftRegistrationNumber((String) row[5])
                        .flightStatus(row[6] != null ? row[6].toString() : null)
                        .availableSeatsCount(((Number) row[7]).longValue())
                        .totalSeatCapacity(((Number) row[8]).intValue())
                        .build())
                .collect(Collectors.toList());
    }

    // ==================== REPORT 2: Passengers per flight ====================
    /**
     * Business Logic:
     * - Counts passengers (tickets with status PAID or RESERVED) per flight
     * - Useful for flight capacity planning and load analysis
     * 
     * Tables Involved: flight, ticket, route
     * 
     * @return List of PassengersPerFlightDTO with passenger counts per flight
     */
    public List<PassengersPerFlightDTO> getPassengersPerFlight() {
        List<Object[]> results = statisticsRepository.findPassengersPerFlight();
        
        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }

        return results.stream()
                .map(row -> PassengersPerFlightDTO.builder()
                        .flightId(((Number) row[0]).longValue())
                        .origin((String) row[1])
                        .destination((String) row[2])
                        .departureTime((Instant) row[3])
                        .flightStatus(row[4] != null ? row[4].toString() : null)
                        .passengerCount(((Number) row[5]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    // ==================== REPORT 3: Seat load factor per flight ====================
    /**
     * Business Logic:
     * - Calculates seat load factor: (sold seats / aircraft.seat_capacity) * 100
     * - Sold seats = flight_seats with status BOOKED or RESERVED
     * - Key metric for airline revenue management
     * 
     * Tables Involved: flight, flight_seat, aircraft, route
     * 
     * @return List of SeatLoadFactorDTO with load factor percentages per flight
     */
    public List<SeatLoadFactorDTO> getSeatLoadFactorPerFlight() {
        List<Object[]> results = statisticsRepository.findSeatLoadFactorPerFlight();
        
        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }

        return results.stream()
                .map(row -> {
                    Long soldSeats = ((Number) row[5]).longValue();
                    Integer totalCapacity = ((Number) row[6]).intValue();
                    Double loadFactor = totalCapacity > 0 
                            ? (soldSeats.doubleValue() / totalCapacity) * 100 
                            : 0.0;
                    
                    return SeatLoadFactorDTO.builder()
                            .flightId(((Number) row[0]).longValue())
                            .origin((String) row[1])
                            .destination((String) row[2])
                            .departureTime((Instant) row[3])
                            .flightStatus(row[4] != null ? row[4].toString() : null)
                            .soldSeatsCount(soldSeats)
                            .totalSeatCapacity(totalCapacity)
                            .loadFactorPercentage(Math.round(loadFactor * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ==================== REPORT 4: Tickets refunded / changed / canceled ====================
    /**
     * Business Logic:
     * - Counts tickets by status: CANCELED, CHANGED
     * - Refunded count equals canceled count (refunds happen on cancellation)
     * - Provides summary for customer service and financial reporting
     * 
     * Tables Involved: ticket
     * 
     * @return TicketStatusSummaryDTO with counts for each status
     */
    public TicketStatusSummaryDTO getTicketStatusSummary() {
        Object[] result = statisticsRepository.findTicketStatusSummary();
        
        if (result == null || result.length == 0) {
            return TicketStatusSummaryDTO.builder()
                    .refundedCount(0L)
                    .changedCount(0L)
                    .canceledCount(0L)
                    .totalAffectedTickets(0L)
                    .build();
        }

        Object[] row = result;
        if (result.length == 1 && result[0] instanceof Object[]) {
            row = (Object[]) result[0];
        }

        Long canceledCount = row.length > 0 && row[0] != null ? ((Number) row[0]).longValue() : 0L;
        Long changedCount = row.length > 1 && row[1] != null ? ((Number) row[1]).longValue() : 0L;
        Long refundedCount = row.length > 2 && row[2] != null ? ((Number) row[2]).longValue() : 0L;

        return TicketStatusSummaryDTO.builder()
                .refundedCount(refundedCount)
                .changedCount(changedCount)
                .canceledCount(canceledCount)
                .totalAffectedTickets(refundedCount + changedCount + canceledCount)
                .build();
    }

    // ==================== REPORT 5a: Revenue by flight ====================
    /**
     * Business Logic:
     * - Sums ticket prices for tickets with status 'PAID' grouped by flight
     * - Key metric for flight profitability analysis
     * 
     * Tables Involved: flight, ticket, route
     * 
     * @return List of RevenueByFlightDTO with revenue per flight
     */
    public List<RevenueByFlightDTO> getRevenueByFlight() {
        List<Object[]> results = statisticsRepository.findRevenueByFlight();

        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }

        return results.stream()
                .map(row -> RevenueByFlightDTO.builder()
                        .flightId(((Number) row[0]).longValue())
                        .origin((String) row[1])
                        .destination((String) row[2])
                        .departureTime((Instant) row[3])
                        .flightStatus(row[4] != null ? row[4].toString() : null)
                        .ticketsSold(((Number) row[5]).longValue())
                        .totalRevenue(row[6] != null ? new BigDecimal(row[6].toString()) : BigDecimal.ZERO)
                        .build())
                .collect(Collectors.toList());
    }

    // ==================== REPORT 5b: Revenue by route ====================
    /**
     * Business Logic:
     * - Sums ticket prices for tickets with status 'PAID' grouped by route
     * - Useful for route profitability and network planning
     * 
     * Tables Involved: route, flight, ticket
     * 
     * @return List of RevenueByRouteDTO with revenue per route
     */
    public List<RevenueByRouteDTO> getRevenueByRoute() {
        List<Object[]> results = statisticsRepository.findRevenueByRoute();

        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }

        return results.stream()
                .map(row -> RevenueByRouteDTO.builder()
                        .routeId(((Number) row[0]).longValue())
                        .origin((String) row[1])
                        .destination((String) row[2])
                        .isExternal((Boolean) row[3])
                        .totalFlights(((Number) row[4]).longValue())
                        .totalTicketsSold(((Number) row[5]).longValue())
                        .totalRevenue(row[6] != null ? new BigDecimal(row[6].toString()) : BigDecimal.ZERO)
                        .build())
                .collect(Collectors.toList());
    }

    // ==================== REPORT 5c: Revenue by time range ====================
    /**
     * Business Logic:
     * - Sums ticket prices for PAID tickets within specified date range
     * - Filters by paidAt timestamp
     * - Useful for period-based financial reporting
     * 
     * Tables Involved: ticket
     * 
     * @param fromDate Start of time range (inclusive)
     * @param toDate End of time range (inclusive)
     * @return RevenueByTimeRangeDTO with aggregated revenue data
     */
    public RevenueByTimeRangeDTO getRevenueByTimeRange(Instant fromDate, Instant toDate) {
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("fromDate and toDate are required");
        }

        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("fromDate must be before or equal to toDate");
        }

        Object[] result = statisticsRepository.findRevenueByTimeRange(fromDate, toDate);

        if (result == null || result[0] == null) {
            return RevenueByTimeRangeDTO.builder()
                    .fromDate(fromDate)
                    .toDate(toDate)
                    .totalTicketsSold(0L)
                    .totalRevenue(BigDecimal.ZERO)
                    .averageTicketPrice(BigDecimal.ZERO)
                    .build();
        }

        Long totalTickets = ((Number) result[0]).longValue();
        BigDecimal totalRevenue = result[1] != null ? new BigDecimal(result[1].toString()) : BigDecimal.ZERO;
        BigDecimal avgPrice = result[2] != null ? new BigDecimal(result[2].toString()).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        return RevenueByTimeRangeDTO.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalTicketsSold(totalTickets)
                .totalRevenue(totalRevenue)
                .averageTicketPrice(avgPrice)
                .build();
    }

    // ==================== REPORT 6: Baggage statistics ====================
    /**
     * Business Logic:
     * - Aggregates baggage data: total count, total weight, overweight count, extra fees
     * - Overweight thresholds: checked > 23kg, carry-on > 7kg
     * - Useful for operations and ancillary revenue analysis
     * 
     * Tables Involved: baggage
     * 
     * @return BaggageStatisticsDTO with aggregated baggage data
     */
    public BaggageStatisticsDTO getBaggageStatistics() {
        Object[] result = statisticsRepository.findBaggageStatistics();

        if (result == null || result[0] == null || ((Number) result[0]).longValue() == 0) {
            return BaggageStatisticsDTO.builder()
                    .totalBaggageCount(0L)
                    .totalWeight(BigDecimal.ZERO)
                    .overweightCount(0L)
                    .totalExtraFee(BigDecimal.ZERO)
                    .carryOnCount(0L)
                    .checkedCount(0L)
                    .build();
        }

        return BaggageStatisticsDTO.builder()
                .totalBaggageCount(((Number) result[0]).longValue())
                .totalWeight(result[1] != null ? new BigDecimal(result[1].toString()) : BigDecimal.ZERO)
                .overweightCount(result[2] != null ? ((Number) result[2]).longValue() : 0L)
                .totalExtraFee(result[3] != null ? new BigDecimal(result[3].toString()) : BigDecimal.ZERO)
                .carryOnCount(result[4] != null ? ((Number) result[4]).longValue() : 0L)
                .checkedCount(result[5] != null ? ((Number) result[5]).longValue() : 0L)
                .build();
    }

    // ==================== REPORT 7: Aircraft status statistics ====================
    /**
     * Business Logic:
     * - Counts aircraft grouped by status (ACTIVE, MAINTENANCE, INACTIVE)
     * - Key metric for fleet management and availability
     * 
     * Tables Involved: aircraft
     * 
     * @return AircraftStatusStatisticsDTO with counts per status
     */
    public AircraftStatusStatisticsDTO getAircraftStatusStatistics() {
        Object[] result = statisticsRepository.findAircraftStatusStatistics();
        
        if (result == null || result.length == 0) {
            return AircraftStatusStatisticsDTO.builder()
                    .activeCount(0L)
                    .maintenanceCount(0L)
                    .inactiveCount(0L)
                    .totalAircraftCount(0L)
                    .build();
        }

        Object[] row = result;
        if (result.length == 1 && result[0] instanceof Object[]) {
            row = (Object[]) result[0];
        }

        Long totalCount = row.length > 3 && row[3] != null ? ((Number) row[3]).longValue() : 0L;
        if (totalCount == 0) {
            return AircraftStatusStatisticsDTO.builder()
                    .activeCount(0L)
                    .maintenanceCount(0L)
                    .inactiveCount(0L)
                    .totalAircraftCount(0L)
                    .build();
        }

        return AircraftStatusStatisticsDTO.builder()
                .activeCount(row.length > 0 && row[0] != null ? ((Number) row[0]).longValue() : 0L)
                .maintenanceCount(row.length > 1 && row[1] != null ? ((Number) row[1]).longValue() : 0L)
                .inactiveCount(row.length > 2 && row[2] != null ? ((Number) row[2]).longValue() : 0L)
                .totalAircraftCount(totalCount)
                .build();
    }

    // ==================== REPORT 8a: Crew per flight ====================
    /**
     * Business Logic:
     * - Counts crew members assigned to each flight
     * - Provides breakdown by position (pilot, copilot, attendant)
     * - Useful for crew scheduling and compliance
     * 
     * Tables Involved: flight, crew_assignment, employee, route
     * 
     * @return List of CrewPerFlightDTO with crew counts per flight
     */
    public List<CrewPerFlightDTO> getCrewPerFlight() {
        List<Object[]> results = statisticsRepository.findCrewPerFlight();
        
        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }

        return results.stream()
                .map(row -> CrewPerFlightDTO.builder()
                        .flightId(((Number) row[0]).longValue())
                        .origin((String) row[1])
                        .destination((String) row[2])
                        .departureTime((Instant) row[3])
                        .flightStatus(row[4] != null ? row[4].toString() : null)
                        .totalCrewCount(row[5] != null ? ((Number) row[5]).longValue() : 0L)
                        .pilotCount(row[6] != null ? ((Number) row[6]).longValue() : 0L)
                        .copilotCount(row[7] != null ? ((Number) row[7]).longValue() : 0L)
                        .attendantCount(row[8] != null ? ((Number) row[8]).longValue() : 0L)
                        .build())
                .collect(Collectors.toList());
    }

    // ==================== REPORT 8b: Employee flight hours ====================
    /**
     * Business Logic:
     * - Retrieves flight hours statistics for each employee
     * - Includes comparison with max allowed hours per month
     * - Shows total flights assigned
     * - Useful for crew fatigue management and compliance
     * 
     * Tables Involved: employee, crew_assignment
     * 
     * @return List of EmployeeFlightHoursDTO with flight hours per employee
     */
    public List<EmployeeFlightHoursDTO> getEmployeeFlightHours() {
        List<Object[]> results = statisticsRepository.findEmployeeFlightHours();
        
        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }

        return results.stream()
                .map(row -> EmployeeFlightHoursDTO.builder()
                        .employeeId(((Number) row[0]).longValue())
                        .fullName((String) row[1])
                        .position(row[2] != null ? row[2].toString() : null)
                        .totalFlightHours(row[3] != null ? ((Number) row[3]).intValue() : 0)
                        .maxFlightHoursPerMonth(row[4] != null ? ((Number) row[4]).intValue() : 0)
                        .totalFlightsAssigned(((Number) row[5]).longValue())
                        .build())
                .collect(Collectors.toList());
    }
}

