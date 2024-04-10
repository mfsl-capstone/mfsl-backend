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
    private List<Player> players;
    public FantasyTeamLineup(Set<Player> players, String playerIdsInOrder) {
        List<Player> playersInOrder = new ArrayList<>();
        if (playerIdsInOrder.split(" ")[0].equals("null")) {
            this.players = players.stream().toList();
            return;
        }
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
