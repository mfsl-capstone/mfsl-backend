package capstone.mfslbackend.repository;

import capstone.mfslbackend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findGamesByDateBetween(LocalDateTime start, LocalDateTime end);
}
