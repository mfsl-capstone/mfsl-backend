package capstone.mfslbackend.repository;

import capstone.mfslbackend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
}
