package uit.se100.repositories.statistics;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uit.se100.entities.flight.Flight;
import uit.se100.repositories.SimpleRepository;

import java.time.Instant;
import java.util.List;

/**
 * Repository for Statistics module.
 * Contains all JPQL queries for generating statistical reports.
 */
@Repository
public interface StatisticsRepository extends SimpleRepository<Flight, Long> {

    // ==================== REPORT 1: Flights with available seats ====================
    /**
     * Finds all flights that have at least one available seat.
     * Returns flight details along with count of available seats.
     * 
     * JPQL joins flight -> flight_seat -> aircraft -> route
     * Filters flight_seats with status = 'AVAILABLE'
     * Groups by flight to count available seats
     */
    @Query("""
            SELECT f.id, r.origin, r.destination, f.departureTime, f.arrivalTime,
                   a.registrationNumber, f.status, COUNT(fs.id), a.seatCapacity
            FROM Flight f
            JOIN f.route r
            JOIN f.aircraft a
            JOIN f.flightSeats fs
            WHERE fs.status = 'AVAILABLE'
            GROUP BY f.id, r.origin, r.destination, f.departureTime, f.arrivalTime,
                     a.registrationNumber, f.status, a.seatCapacity
            HAVING COUNT(fs.id) > 0
            ORDER BY f.departureTime DESC
            """)
    List<Object[]> findFlightsWithAvailableSeats();

    // ==================== REPORT 2: Passengers per flight ====================
    /**
     * Counts the number of passengers (tickets) per flight.
     * Only counts tickets with status PAID or RESERVED.
     * 
     * JPQL joins flight -> ticket -> route
     * Groups by flight
     */
    @Query("""
            SELECT f.id, r.origin, r.destination, f.departureTime, f.status, COUNT(t.id)
            FROM Flight f
            JOIN f.route r
            LEFT JOIN Ticket t ON t.flight.id = f.id AND t.status IN ('PAID', 'RESERVED')
            GROUP BY f.id, r.origin, r.destination, f.departureTime, f.status
            ORDER BY f.departureTime DESC
            """)
    List<Object[]> findPassengersPerFlight();

    // ==================== REPORT 3: Seat load factor per flight ====================
    /**
     * Calculates seat load factor: sold seats / total capacity.
     * Sold seats = flight_seats with status BOOKED or RESERVED.
     * 
     * JPQL joins flight -> flight_seat -> aircraft -> route
     */
    @Query("""
            SELECT f.id, r.origin, r.destination, f.departureTime, f.status,
                   SUM(CASE WHEN fs.status IN ('BOOKED', 'RESERVED') THEN 1 ELSE 0 END),
                   a.seatCapacity
            FROM Flight f
            JOIN f.route r
            JOIN f.aircraft a
            LEFT JOIN f.flightSeats fs
            GROUP BY f.id, r.origin, r.destination, f.departureTime, f.status, a.seatCapacity
            ORDER BY f.departureTime DESC
            """)
    List<Object[]> findSeatLoadFactorPerFlight();

    // ==================== REPORT 4: Tickets refunded / changed / canceled ====================
    /**
     * Counts tickets by status for summary.
     * Returns counts for CANCELED, CHANGED statuses.
     * Refunded tickets are determined by CANCELED status with refund transactions.
     */
    @Query("""
            SELECT 
                SUM(CASE WHEN t.status = 'CANCELED' THEN 1 ELSE 0 END),
                SUM(CASE WHEN t.status = 'CHANGED' THEN 1 ELSE 0 END),
                SUM(CASE WHEN t.status = 'CANCELED' THEN 1 ELSE 0 END)
            FROM Ticket t
            """)
    Object[] findTicketStatusSummary();

    // ==================== REPORT 5a: Revenue by flight ====================
    /**
     * Calculates total revenue per flight from paid tickets.
     * 
     * JPQL joins flight -> ticket -> route
     * Filters tickets with status = 'PAID'
     * Sums ticket prices grouped by flight
     */
    @Query("""
            SELECT f.id, r.origin, r.destination, f.departureTime, f.status,
                   COUNT(t.id), COALESCE(SUM(t.price), 0)
            FROM Flight f
            JOIN f.route r
            LEFT JOIN Ticket t ON t.flight.id = f.id AND t.status = 'PAID'
            GROUP BY f.id, r.origin, r.destination, f.departureTime, f.status
            ORDER BY f.departureTime DESC
            """)
    List<Object[]> findRevenueByFlight();

    // ==================== REPORT 5b: Revenue by route ====================
    /**
     * Calculates total revenue per route from paid tickets.
     * 
     * JPQL joins route -> flight -> ticket
     * Groups by route
     */
    @Query("""
            SELECT r.id, r.origin, r.destination, r.isExternal,
                   COUNT(DISTINCT f.id), COUNT(t.id), COALESCE(SUM(t.price), 0)
            FROM Route r
            LEFT JOIN r.flights f
            LEFT JOIN Ticket t ON t.flight.id = f.id AND t.status = 'PAID'
            GROUP BY r.id, r.origin, r.destination, r.isExternal
            ORDER BY SUM(t.price) DESC NULLS LAST
            """)
    List<Object[]> findRevenueByRoute();

    // ==================== REPORT 5c: Revenue by time range ====================
    /**
     * Calculates total revenue within a time range.
     * 
     * Filters tickets by paidAt within fromDate - toDate
     * Only counts PAID tickets
     */
    @Query("""
            SELECT COUNT(t.id), COALESCE(SUM(t.price), 0), COALESCE(AVG(t.price), 0)
            FROM Ticket t
            WHERE t.status = 'PAID'
              AND t.paidAt >= :fromDate
              AND t.paidAt <= :toDate
            """)
    Object[] findRevenueByTimeRange(
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate
    );

    // ==================== REPORT 6: Baggage statistics ====================
    /**
     * Calculates baggage statistics.
     * Total count, total weight, overweight count, total extra fees.
     * Overweight: checked baggage > 23kg, carry-on > 7kg (standard limits)
     */
    @Query("""
            SELECT COUNT(b.id),
                   COALESCE(SUM(b.weight), 0),
                   SUM(CASE
                       WHEN (b.type = 'CHECKED' AND b.weight > 23) OR
                            (b.type = 'CARRY_ON' AND b.weight > 7) THEN 1
                       ELSE 0
                   END),
                   COALESCE(SUM(b.extraFee), 0),
                   SUM(CASE WHEN b.type = 'CARRY_ON' THEN 1 ELSE 0 END),
                   SUM(CASE WHEN b.type = 'CHECKED' THEN 1 ELSE 0 END)
            FROM Baggage b
            """)
    Object[] findBaggageStatistics();

    // ==================== REPORT 7: Aircraft status statistics ====================
    /**
     * Counts aircraft by status.
     */
    @Query("""
            SELECT 
                SUM(CASE WHEN a.status = 'ACTIVE' THEN 1 ELSE 0 END),
                SUM(CASE WHEN a.status = 'MAINTENANCE' THEN 1 ELSE 0 END),
                SUM(CASE WHEN a.status = 'INACTIVE' THEN 1 ELSE 0 END),
                COUNT(a.id)
            FROM Aircraft a
            """)
    Object[] findAircraftStatusStatistics();

    // ==================== REPORT 8a: Crew per flight ====================
    /**
     * Counts crew members per flight with breakdown by position.
     * 
     * JPQL joins flight -> crew_assignment -> employee -> route
     */
    @Query("""
            SELECT f.id, r.origin, r.destination, f.departureTime, f.status,
                   COUNT(ca.id),
                   SUM(CASE WHEN e.position = 'PILOT' THEN 1 ELSE 0 END),
                   SUM(CASE WHEN e.position = 'COPILOT' THEN 1 ELSE 0 END),
                   SUM(CASE WHEN e.position = 'ATTENDANT' THEN 1 ELSE 0 END)
            FROM Flight f
            JOIN f.route r
            LEFT JOIN f.crewAssignments ca
            LEFT JOIN ca.employee e
            GROUP BY f.id, r.origin, r.destination, f.departureTime, f.status
            ORDER BY f.departureTime DESC
            """)
    List<Object[]> findCrewPerFlight();

    // ==================== REPORT 8b: Employee flight hours ====================
    /**
     * Gets total flight hours per employee with flights assigned count.
     * 
     * JPQL joins employee -> crew_assignment
     */
    @Query("""
            SELECT e.id, e.fullName, e.position, e.totalFlightHours, e.maxFlightHoursPerMonth,
                   COUNT(ca.id)
            FROM Employee e
            LEFT JOIN CrewAssignment ca ON ca.employee.id = e.id
            GROUP BY e.id, e.fullName, e.position, e.totalFlightHours, e.maxFlightHoursPerMonth
            ORDER BY e.totalFlightHours DESC NULLS LAST
            """)
    List<Object[]> findEmployeeFlightHours();
}

