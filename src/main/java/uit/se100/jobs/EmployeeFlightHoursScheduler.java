package uit.se100.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.repositories.employee.EmployeeRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmployeeFlightHoursScheduler {

    private final EmployeeRepository employeeRepository;

    /**
     * Reset all employees' totalFlightHours to 0 at the start of every month.
     * Cron: 0 0 0 1 * * (At 00:00:00 on day 1 of every month)
     */
    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void resetMonthlyFlightHours() {
        log.info("Running scheduled task: Reset all employees' totalFlightHours");
        try {
            int affectedRows = employeeRepository.resetAllTotalFlightHours();
            log.info("Successfully reset totalFlightHours for {} employees", affectedRows);
        } catch (Exception e) {
            log.error("Error while resetting employees' totalFlightHours: {}", e.getMessage(), e);
        }
    }
}

