package uit.se100.entities.assign;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.employee.Employee;
import uit.se100.entities.flight.Flight;

@Getter
@Setter
@Entity
@Table(name = "crew_assignments")
public class CrewAssignment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "flight_id", nullable = false)
  private Flight flight;

  @ManyToOne
  @JoinColumn(name = "employee_id", nullable = false)
  private Employee employee;
}
