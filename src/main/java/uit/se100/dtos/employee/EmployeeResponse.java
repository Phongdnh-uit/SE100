package uit.se100.dtos.employee;

import uit.se100.enums.employee.EmployeePosition;

public record EmployeeResponse(
        Long id,

        String fullName,


        EmployeePosition position,

        String workExperience,

        Long totalFlightHours,

        Integer maxFlightHoursPerMonth
) {
}
