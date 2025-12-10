package uit.se100.hooks.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uit.se100.dtos.employee.EmployeeRequest;
import uit.se100.dtos.employee.EmployeeResponse;
import uit.se100.dtos.user.UserRequest;
import uit.se100.dtos.user.UserResponse;
import uit.se100.entities.authentication.User;
import uit.se100.entities.employee.Employee;
import uit.se100.enums.RoleEnum;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.hooks.GenericHook;
import uit.se100.repositories.authentication.UserRepository;
import uit.se100.services.CrudService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployeeHook implements GenericHook<Employee, Long, EmployeeRequest, EmployeeResponse> {
    private final CrudService<User, Long, UserRequest, UserResponse> userService;
    private final UserRepository userRepository;

    @Override
    public void enrichCreate(EmployeeRequest input, Employee entity, Map<String, Object> context) {
        UserRequest userRequest = input.accountRequest();
        userRequest.setRole(RoleEnum.EMPLOYEE);

//        Save user request
        UserResponse userResponse = userService.create(userRequest);

//        update user in entity
        User userInDb = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));

        entity.setUser(userInDb);

//        Add default data
        addDefaultDate(entity);
    }

    public void addDefaultDate(Employee entity) {
        if (entity.getTotalFlightHours() == null) entity.setTotalFlightHours(0);
    }

}
