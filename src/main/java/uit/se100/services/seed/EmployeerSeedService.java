package uit.se100.services.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.dtos.employee.EmployeeRequest;
import uit.se100.dtos.employee.EmployeeResponse;
import uit.se100.entities.employee.Employee;
import uit.se100.services.CrudService;
import uit.se100.utils.JsonSeedReader;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeerSeedService {

    private final JsonSeedReader jsonSeedReader;
    private final CrudService<Employee, Long, EmployeeRequest, EmployeeResponse> employeeService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void seed() {
        List<EmployeeRequest> dtos =
                jsonSeedReader.readList("seed/employee.json", EmployeeRequest.class);

        dtos.forEach(employeeService::create);

        log.info("Seeded {} employee", dtos.size());
    }
}
