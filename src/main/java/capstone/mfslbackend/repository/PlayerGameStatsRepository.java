package capstone.mfslbackend.repository;

import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.PlayerGameStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerGameStatsRepository extends JpaRepository<PlayerGameStats, Long> {
    List<PlayerGameStats> findByPlayer(Player player);
}
