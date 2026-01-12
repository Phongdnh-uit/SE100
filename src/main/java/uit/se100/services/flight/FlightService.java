package uit.se100.services.flight;

import org.springframework.data.domain.Pageable;
import uit.se100.dtos.PageResponse;
import uit.se100.dtos.flight.AssignSeatRequest;
import uit.se100.dtos.flight.FlightResponse;
import uit.se100.entities.flight.FlightSeat;
import uit.se100.enums.seat.SeatClass;

public interface FlightService {
    // Internal method to update flight status when seat information changes
    void updateFlightStatusWhenSeatChanged(FlightSeat seat);

    void delayFlight(Long flightId, Long delayMinutes);


    PageResponse<FlightSeat> getSeatAvailable(Long flightId, SeatClass seatClass, Pageable pageable);

    FlightResponse assignSeat(Long flightId, AssignSeatRequest assignSeatRequest);

    void cancelFlight(Long flightId);

    // Auto update flights to DEPARTED status when departure time has passed
    void updateFlightsToDeparted();

    // Auto update flights to COMPLETED status when arrival time has passed
    void updateFlightsToCompleted();
}
