package uit.se100.mappers.employee;

import org.mapstruct.*;
import uit.se100.dtos.employee.EmployeeRequest;
import uit.se100.dtos.employee.EmployeeResponse;
import uit.se100.entities.employee.Employee;
import uit.se100.mappers.GenericMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeMapper extends GenericMapper<Employee, EmployeeRequest, EmployeeResponse> {
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(EmployeeRequest request, @MappingTarget Employee entity);
}
