package uit.se100.mappers.schedule;

import org.mapstruct.Mapper;
import uit.se100.dtos.schedule.ScheduleRequest;
import uit.se100.dtos.schedule.ScheduleResponse;
import uit.se100.entities.schedule.Schedule;
import uit.se100.mappers.GenericMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ScheduleMapper
    extends GenericMapper<Schedule, ScheduleRequest, ScheduleResponse> {}
