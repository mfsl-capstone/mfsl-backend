package capstone.mfslbackend.repository;

import capstone.mfslbackend.model.FantasyLeague;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FantasyLeagueRepository extends JpaRepository<FantasyLeague, Long> {
    List<FantasyLeague> findFantasyLeagueByLeagueNameLikeIgnoreCase(String leagueName);
}
