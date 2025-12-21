package uit.se100.dtos.aircraft;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uit.se100.enums.aircraft.AircraftStatus;

@Getter
@Setter
public class AircraftRequest {
  @NotBlank private String type;

  @NotNull private Integer seatCapacity;

  @NotBlank private String registrationNumber;

  @NotBlank private String manufacturer;

  @NotBlank private String model;

  @NotNull private Integer manufactureYear;

  @NotBlank private String serialNumber;

  @NotNull private AircraftStatus status;
}
