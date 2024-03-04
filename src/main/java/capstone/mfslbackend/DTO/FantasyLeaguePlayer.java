package capstone.mfslbackend.DTO;

import capstone.mfslbackend.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FantasyLeaguePlayer {
    private Player player;
    private Boolean taken;
}
