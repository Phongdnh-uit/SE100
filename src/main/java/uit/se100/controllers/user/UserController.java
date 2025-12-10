package uit.se100.controllers.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import uit.se100.controllers.GenericController;
import uit.se100.dtos.employee.EmployeeRequest;
import uit.se100.dtos.employee.EmployeeResponse;
import uit.se100.dtos.user.UserRequest;
import uit.se100.dtos.user.UserResponse;
import uit.se100.entities.authentication.User;
import uit.se100.entities.employee.Employee;
import uit.se100.services.CrudService;

@Tag(name = "User")
@RequestMapping("/users")
@RestController
public class UserController extends GenericController<User, Long, UserRequest, UserResponse> {

    private final CrudService<Employee, Long, EmployeeRequest, EmployeeResponse> employeeService;

    public UserController(CrudService<User, Long, UserRequest, UserResponse> service, CrudService<Employee, Long, EmployeeRequest, EmployeeResponse> employeeService) {
        super(service);
        this.employeeService = employeeService;
    }

    @PutMapping("/{user_id}/employee")
    public EmployeeResponse promoteToEmployee(@PathVariable Long user_id, @Valid @RequestBody EmployeeRequest employeeRequest) {
        return this.employeeService.create(employeeRequest);
    }

}
