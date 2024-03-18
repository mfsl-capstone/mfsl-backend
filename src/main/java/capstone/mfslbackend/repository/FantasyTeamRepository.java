package capstone.mfslbackend.repository;

import capstone.mfslbackend.model.FantasyTeam;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FantasyTeamRepository extends JpaRepository<FantasyTeam, Long> {
}
