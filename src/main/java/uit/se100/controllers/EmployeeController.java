package uit.se100.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uit.se100.dtos.employee.EmployeeRequest;
import uit.se100.dtos.employee.EmployeeResponse;
import uit.se100.entities.employee.Employee;
import uit.se100.services.CrudService;

@Tag(name = "Employee")
@RequestMapping("/employees")
@RestController
public class EmployeeController extends GenericController<Employee, Long, EmployeeRequest, EmployeeResponse> {

    public EmployeeController(CrudService<Employee, Long, EmployeeRequest, EmployeeResponse> service) {
        super(service);
    }

    
}
