package uit.se100.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * A simple repository interface that extends JpaRepository and JpaSpecificationExecutor.
 * @param E Entity type
 * @param ID ID type of the entity
 */
@NoRepositoryBean
public interface SimpleRepository<E, ID>
    extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {}
