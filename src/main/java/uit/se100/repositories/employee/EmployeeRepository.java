package uit.se100.repositories.employee;

import org.springframework.stereotype.Repository;
import uit.se100.entities.employee.Employee;
import uit.se100.repositories.SimpleRepository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends SimpleRepository<Employee, Long> {
    Optional<Employee> findByUserId(Long id);
}
