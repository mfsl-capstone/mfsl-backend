package capstone.mfslbackend.repository;
import capstone.mfslbackend.model.FantasyWeek;
import capstone.mfslbackend.model.Game;
import capstone.mfslbackend.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface FantasyWeekRepository extends JpaRepository<FantasyWeek, Long>, JpaSpecificationExecutor<FantasyWeek> {
}
