package uit.se100.controllers.schedule;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uit.se100.controllers.GenericController;
import uit.se100.dtos.schedule.ScheduleRequest;
import uit.se100.dtos.schedule.ScheduleResponse;
import uit.se100.entities.schedule.Schedule;
import uit.se100.services.CrudService;

@Tag(name = "Schedule")
@RequestMapping("/schedules")
@RestController
public class ScheduleController
    extends GenericController<Schedule, Long, ScheduleRequest, ScheduleResponse> {

  public ScheduleController(
      CrudService<Schedule, Long, ScheduleRequest, ScheduleResponse> service) {
    super(service);
  }
}
