package capstone.mfslbackend.repository;

import capstone.mfslbackend.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PlayerRepository extends JpaRepository<Player, Long> {
}
