package uit.se100.services.seed;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.repositories.aircraft.AircraftRepository;
import uit.se100.repositories.assign.AssignmentRepository;
import uit.se100.repositories.authentication.RefreshTokenRepository;
import uit.se100.repositories.authentication.UserRepository;
import uit.se100.repositories.authentication.VerificationRepository;
import uit.se100.repositories.baggage.BaggageRepository;
import uit.se100.repositories.employee.EmployeeRepository;
import uit.se100.repositories.flight.FlightRepository;
import uit.se100.repositories.flight.FlightSeatRepository;
import uit.se100.repositories.passenger.PassengerRepository;
import uit.se100.repositories.payment.TransactionRepository;
import uit.se100.repositories.route.RouteRepository;
import uit.se100.repositories.seat.SeatRepository;
import uit.se100.repositories.ticket.TicketRepository;

@Service
@RequiredArgsConstructor
public class DatabaseCleanupService {

    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final FlightRepository flightRepository;
    private final RouteRepository routeRepository;
    private final AircraftRepository aircraftRepository;
    private final PassengerRepository passengerRepository;
    private final EmployeeRepository employeeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final BaggageRepository baggageRepository;
    private final AssignmentRepository assignmentRepository;
    private final FlightSeatRepository flightSeatRepository;
    private final TransactionRepository transactionRepository;
    private final VerificationRepository verificationRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteTables() {
        transactionRepository.deleteAllInBatch();
        baggageRepository.deleteAllInBatch();
        ticketRepository.deleteAllInBatch();

        passengerRepository.deleteAllInBatch();
        employeeRepository.deleteAllInBatch();   // bật lại nếu cần

        flightSeatRepository.deleteAllInBatch();
        assignmentRepository.deleteAllInBatch();

        flightRepository.deleteAllInBatch();
        seatRepository.deleteAllInBatch();
        routeRepository.deleteAllInBatch();
        aircraftRepository.deleteAllInBatch();

        refreshTokenRepository.deleteAllInBatch();
        verificationRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }


}
