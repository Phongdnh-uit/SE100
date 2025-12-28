package uit.se100.services.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.repositories.aircraft.AircraftRepository;
import uit.se100.repositories.authentication.RefreshTokenRepository;
import uit.se100.repositories.authentication.UserRepository;
import uit.se100.repositories.employee.EmployeeRepository;
import uit.se100.repositories.flight.FlightRepository;
import uit.se100.repositories.passenger.PassengerRepository;
import uit.se100.repositories.route.RouteRepository;
import uit.se100.repositories.schedule.ScheduleRepository;
import uit.se100.repositories.seat.SeatRepository;
import uit.se100.repositories.ticket.TicketRepository;

@Service
@RequiredArgsConstructor
public class DatabaseCleanupService {

    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final ScheduleRepository scheduleRepository;
    private final FlightRepository flightRepository;
    private final RouteRepository routeRepository;
    private final AircraftRepository aircraftRepository;
    private final PassengerRepository passengerRepository;
    private final EmployeeRepository employeeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteTables() {
        ticketRepository.deleteAll();
        seatRepository.deleteAll();
        scheduleRepository.deleteAll();
        flightRepository.deleteAll();
        routeRepository.deleteAll();
        aircraftRepository.deleteAll();
        passengerRepository.deleteAll();
        employeeRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }
}
