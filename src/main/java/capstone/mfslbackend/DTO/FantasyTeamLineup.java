package capstone.mfslbackend.DTO;

import capstone.mfslbackend.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FantasyTeamLineup {
    List<Player> players;
    public FantasyTeamLineup(Set<Player> players, String playerIdsInOrder) {
        List<Player> playersInOrder = new ArrayList<>();
        String[] playerIds = playerIdsInOrder.split(" ");
        for (String playerId : playerIds) {
            for (Player player : players) {
                if (player.getPlayerId().equals(Long.parseLong(playerId))) {
                    playersInOrder.add(player);
                    break;
                }
            }
        }
        this.players = playersInOrder;
    }
}
