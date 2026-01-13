package uit.se100.dtos.passenger;

import uit.se100.enums.passenger.TierEnum;

import java.time.LocalDate;

public record PassengerResponse(
        Long id,

        String fullName,

        LocalDate dateOfBirth,

        String nationality,

        String idNumber,

        String address,

        String phone,

        TierEnum tier
) {
}
