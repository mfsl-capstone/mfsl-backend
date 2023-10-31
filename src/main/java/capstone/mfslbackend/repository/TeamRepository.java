package capstone.mfslbackend.repository;

import capstone.mfslbackend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}

