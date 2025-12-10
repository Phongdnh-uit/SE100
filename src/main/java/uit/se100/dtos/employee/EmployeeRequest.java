package uit.se100.dtos.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uit.se100.dtos.user.UserRequest;
import uit.se100.enums.employee.EmployeePosition;

public record EmployeeRequest(

        @NotNull
        UserRequest accountRequest,

        @NotBlank
        @Size(max = 100)
        String fullName,

        @NotNull
        EmployeePosition position,

        @NotBlank
        @Size(max = 50)
        String workExperience
) {
}
