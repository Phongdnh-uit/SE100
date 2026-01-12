package uit.se100.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uit.se100.services.flight.FlightService;

@Slf4j
@RequiredArgsConstructor
@Component
public class FlightStatusScheduler {

  private final FlightService flightService;

  @Scheduled(cron = "0 */5 * * * *")
  public void autoUpdateFlightsToDeparted() {
    log.debug("Running scheduled task: Auto update flights to DEPARTED status");
    try {
      flightService.updateFlightsToDeparted();
    } catch (Exception e) {
      log.error("Error while updating flights to DEPARTED status: {}", e.getMessage(), e);
    }
  }

  @Scheduled(cron = "30 */5 * * * *")
  public void autoUpdateFlightsToCompleted() {
    log.debug("Running scheduled task: Auto update flights to COMPLETED status");
    try {
      flightService.updateFlightsToCompleted();
    } catch (Exception e) {
      log.error("Error while updating flights to COMPLETED status: {}", e.getMessage(), e);
    }
  }
}
