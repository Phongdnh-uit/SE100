package uit.se100.dtos.passenger;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import uit.se100.dtos.user.UserRequest;

import java.time.LocalDate;

public record PassengerRequest(

        @NotNull
        UserRequest accountRequest,

        @NotBlank
        @Size(max = 100)
        String fullName,

        @NotNull
        @Past
        LocalDate dateOfBirth,

        @NotBlank
        @Size(max = 50)
        String nationality,

        @NotBlank
        @Size(max = 50)
        String idNumber,

        @NotBlank
        String address,

        @NotBlank
        @Size(max = 20)
        String phone
) {
}
