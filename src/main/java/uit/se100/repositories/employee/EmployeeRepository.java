package uit.se100.repositories.employee;

import org.springframework.stereotype.Repository;
import uit.se100.entities.employee.Employee;
import uit.se100.repositories.SimpleRepository;

@Repository
public interface EmployeeRepository extends SimpleRepository<Employee, Long> {
}
