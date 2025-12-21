package uit.se100.repositories.schedule;

import org.springframework.stereotype.Repository;
import uit.se100.entities.schedule.Schedule;
import uit.se100.repositories.SimpleRepository;

@Repository
public interface ScheduleRepository extends SimpleRepository<Schedule, Long> {}
