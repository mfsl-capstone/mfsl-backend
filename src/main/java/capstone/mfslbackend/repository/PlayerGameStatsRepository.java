package capstone.mfslbackend.repository;

import capstone.mfslbackend.model.PlayerGameStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerGameStatsRepository extends JpaRepository<PlayerGameStats, Long> {
}
