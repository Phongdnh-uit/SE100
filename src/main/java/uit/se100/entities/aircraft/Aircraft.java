package uit.se100.entities.aircraft;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.entities.flight.Flight;
import uit.se100.enums.aircraft.AircraftStatus;

@Getter
@Setter
@Entity
@Table(name = "aircrafts")
public class Aircraft extends BaseEntity {
  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private Integer seatCapacity;

  @Column(nullable = false, unique = true)
  private String registrationNumber;

  @Column(nullable = false)
  private String manufacturer;

  @Column(nullable = false)
  private String model;

  @Column(nullable = false)
  private Integer manufactureYear;

  @Column(nullable = false, unique = true)
  private String serialNumber;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AircraftStatus status;

  @OneToMany(mappedBy = "aircraft")
  private List<Flight> flights = new ArrayList<>();
}
