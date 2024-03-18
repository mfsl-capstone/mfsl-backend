package capstone.mfslbackend.repository;

import capstone.mfslbackend.model.FantasyTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FantasyTeamRepository extends JpaRepository<FantasyTeam, Long> {
    List<FantasyTeam> findFantasyTeamsByFantasyLeagueId(Long leagueId);
}
