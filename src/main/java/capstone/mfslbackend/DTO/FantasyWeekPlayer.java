package capstone.mfslbackend.DTO;

import capstone.mfslbackend.model.Game;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.PlayerGameStats;
import lombok.Data;

import java.util.List;
@Data
public class FantasyWeekPlayer {
    private PlayerWithNoStatsDTO player;
    private List<PlayerGameStats> stats;
    private String display;
    public FantasyWeekPlayer from(Player player, List<PlayerGameStats> stats, List<Game> games) {
        this.player = new PlayerWithNoStatsDTO().from(player);
        this.stats = stats;
        StringBuilder display = new StringBuilder();
        int score = 0;
        for (PlayerGameStats stat : stats) {
            score += stat.getPoints();
        }
        display.append(score);
        for (Game game : games) {
            display.append(",");
            if (game.getHomeTeam().getTeamId().equals(player.getTeam().getTeamId())) {
                display.append(game.getAwayTeam().getName()).append(" ");
            } else if (game.getAwayTeam().getTeamId().equals(player.getTeam().getTeamId())) {
                display.append(game.getHomeTeam().getName()).append(" ");
            }
        }
        this.display = display.toString().strip();
        return this;
    }
}
