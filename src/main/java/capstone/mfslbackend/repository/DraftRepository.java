package capstone.mfslbackend.repository;

import capstone.mfslbackend.model.Draft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DraftRepository extends JpaRepository<Draft, Long>, JpaSpecificationExecutor<Draft> {
}
