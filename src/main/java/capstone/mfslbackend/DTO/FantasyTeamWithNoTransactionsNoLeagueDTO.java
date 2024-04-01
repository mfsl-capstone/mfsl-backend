package capstone.mfslbackend.DTO;

import capstone.mfslbackend.model.FantasyTeam;
import lombok.Data;

@Data
public class FantasyTeamWithNoTransactionsNoLeagueDTO {
    private long id;
    private String teamName;
    private int orderNumber;
    private String colour;
    private int wins;
    private int losses;
    private int ties;
    private int points;
    private int fantasyPoints;
    public FantasyTeamWithNoTransactionsNoLeagueDTO from(FantasyTeam fantasyTeam) {
        this.id = fantasyTeam.getId();
        this.teamName = fantasyTeam.getTeamName();
        this.orderNumber = fantasyTeam.getOrderNumber();
        this.colour = fantasyTeam.getColour();
        this.wins = fantasyTeam.getWins();
        this.losses = fantasyTeam.getLosses();
        this.ties = fantasyTeam.getTies();
        this.points = fantasyTeam.getPoints();
        this.fantasyPoints = fantasyTeam.getFantasyPoints();
        return this;
    }
}
