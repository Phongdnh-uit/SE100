package uit.se100.dtos.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import uit.se100.dtos.user.UserRequest;
import uit.se100.enums.employee.EmployeePosition;

@Data
public class EmployeeRequest {

    // For who want to create new account
    private UserRequest accountRequest;

    // Account already exists
    private Long accountId;

    @NotBlank
    @Size(max = 100)
    private String fullName;

    @NotNull
    private EmployeePosition position;

    @NotBlank
    @Size(max = 50)
    private String workExperience;
}
