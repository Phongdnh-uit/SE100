package uit.se100.hooks.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uit.se100.dtos.PageResponse;
import uit.se100.dtos.user.UserRequest;
import uit.se100.dtos.user.UserResponse;
import uit.se100.entities.authentication.User;
import uit.se100.entities.employee.Employee;
import uit.se100.entities.passenger.Passenger;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.hooks.GenericHook;
import uit.se100.repositories.authentication.UserRepository;
import uit.se100.repositories.employee.EmployeeRepository;
import uit.se100.repositories.passenger.PassengerRepository;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class UserHook implements GenericHook<User, Long, UserRequest, UserResponse> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PassengerRepository passengerRepository;
    private final EmployeeRepository employeeRepository;

    public void unlinkEmployeeFromAccount(Long employeeId) {
        Employee emp = employeeRepository.findByUserId(employeeId).orElse(null);

        if (emp == null) return;

        emp.setUser(null);
    }

    public void unlinkPassengerFromAccount(Long passengerId) {
        Passenger p = passengerRepository.findByUserId(passengerId).orElse(null);

        if (p == null) return;

        p.setUser(null);
    }

    @Override
    public void enrichFindAll(PageResponse<UserResponse> response) {
    }

    @Override
    public void enrichFindById(UserResponse response) {
    }

    @Override
    public void validateCreate(UserRequest input, Map<String, Object> context) {
        validate(input, null);
    }

    @Override
    public void enrichCreate(UserRequest input, User entity, Map<String, Object> context) {
        enrichCreate(input, entity);
    }

    @Override
    public void afterCreate(User entity, UserResponse response, Map<String, Object> context) {
    }

    @Override
    public void validateUpdate(
            Long id, UserRequest input, User existingEntity, Map<String, Object> context) {
        validate(input, id);
    }

    @Override
    public void enrichUpdate(UserRequest input, User entity, Map<String, Object> context) {
    }

    @Override
    public void afterUpdate(User entity, UserResponse response, Map<String, Object> context) {
    }

    @Override
    public void validateDelete(Long id) {
        this.unlinkEmployeeFromAccount(id);
        this.unlinkPassengerFromAccount(id);
    }

    @Override
    public void afterDelete(Long id) {

    }

    @Override
    public void validateBulkDelete(Iterable<Long> ids) {
    }

    @Override
    public void afterBulkDelete(Iterable<Long> ids) {
    }

    private void validate(UserRequest request, Long id) {
        Map<String, String> errors = new HashMap<>();

        // Check unique username
        Specification<User> usernameSpec =
                (root, _, builder) -> builder.equal(root.get("username"), request.getUsername());
        if (id != null) {
            usernameSpec = usernameSpec.and((root, _, builder) -> builder.notEqual(root.get("id"), id));
        }

        if (userRepository.count(usernameSpec) > 0) {
            errors.put("username", "Username is already taken");
        }

        // Check unique email
        Specification<User> emailSpec =
                (root, _, builder) -> builder.equal(root.get("email"), request.getEmail());
        if (id != null) {
            emailSpec = emailSpec.and((root, _, builder) -> builder.notEqual(root.get("id"), id));
        }
        if (userRepository.count(emailSpec) > 0) {
            errors.put("email", "Email is already taken");
        }

        // Check unique phone number
        Specification<User> phoneSpec =
                (root, _, builder) -> builder.equal(root.get("phone"), request.getPhone());
        if (id != null) {
            phoneSpec = phoneSpec.and((root, _, builder) -> builder.notEqual(root.get("id"), id));
        }
        if (userRepository.count(phoneSpec) > 0) {
            errors.put("phone", "Phone number is already taken");
        }

        if (!errors.isEmpty()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, errors);
        }
    }

    private void enrichCreate(UserRequest request, User entity) {
        // Encrypt password
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        entity.setPasswordHash(encodedPassword);
    }
}
