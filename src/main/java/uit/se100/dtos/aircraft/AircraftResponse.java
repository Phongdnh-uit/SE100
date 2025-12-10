package uit.se100.dtos.aircraft;

import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.enums.aircraft.AircraftStatus;

@Getter
@Setter
public class AircraftResponse extends BaseEntity {
  private String type;

  private Integer seatCapacity;

  private String registrationNumber;

  private String manufacturer;

  private String model;

  private Integer manufactureYear;

  private String serialNumber;

  private AircraftStatus status;
}
