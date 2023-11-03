package capstone.mfslbackend.repository;

import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Modifying
    @Query("update Player p set p.number = ?2 where p.playerId = ?1")
    void updatePlayerNumberById(@Param("playerId") Long playerId, @Param("number") int number);

    @Modifying
    @Query("update Player p set p.position = ?2 where p.playerId = ?1")
    void updatePlayerPositionById(@Param("playerId") Long playerId, @Param("position")String position);

    @Modifying
    @Query("update Player p set p.team = ?2 where p.playerId = ?1")
    void updatePlayerTeamById(@Param("playerId") Long playerId, @Param("position") Team team);
}
