package uit.se100.services.flight;

import uit.se100.entities.flight.FlightSeat;

public interface FlightService {
  // Internal method to update flight status when seat information changes
  void updateFlightStatusWhenSeatChanged(FlightSeat seat);

  void delayFlight(Long flightId, Long delayMinutes);

  void cancelFlight(Long flightId);
}
