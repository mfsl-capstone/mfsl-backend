package capstone.mfslbackend.DTO;

import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.Team;
import lombok.Data;

@Data
public class PlayerWithNoStatsDTO {
    private String name;
    private String position;
    private String url;
    private Integer number;
    private Integer points;
    private Team team;
    public PlayerWithNoStatsDTO from(Player player) {
        this.name = player.getName();
        this.position = player.getPosition();
        this.url = player.getUrl();
        this.number = player.getNumber();
        this.points = player.getPoints();
        this.team = player.getTeam();
        return this;
    }
}
