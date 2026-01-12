package uit.se100.repositories.employee;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uit.se100.entities.employee.Employee;
import uit.se100.repositories.SimpleRepository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends SimpleRepository<Employee, Long> {
    Optional<Employee> findByUserId(Long id);

    @Modifying
    @Query("UPDATE Employee e SET e.totalFlightHours = 0")
    int resetAllTotalFlightHours();
}
