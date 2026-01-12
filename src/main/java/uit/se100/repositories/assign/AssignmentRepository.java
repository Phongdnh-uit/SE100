package uit.se100.repositories.assign;

import org.springframework.stereotype.Repository;
import uit.se100.entities.assign.CrewAssignment;
import uit.se100.repositories.SimpleRepository;

@Repository
public interface AssignmentRepository extends SimpleRepository<CrewAssignment, Long> {}
