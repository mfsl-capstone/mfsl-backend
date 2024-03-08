package capstone.mfslbackend.repository;
import capstone.mfslbackend.model.FantasyWeek;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FantasyWeekRepository extends JpaRepository<FantasyWeek, Long> {
    List<FantasyWeek> findByWeekNumber(int weekNumber);

    List<FantasyWeek> findAllByFantasyLeagueId(Long leagueId);


}
