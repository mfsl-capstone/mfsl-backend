package capstone.mfslbackend.DTO;

import capstone.mfslbackend.model.FantasyWeek;
import lombok.Data;

@Data
public class FantasyWeekForTableDTO {
    private int weekNumber;
    private String fantasyTeamA;
    private String fantasyTeamB;
    private int teamAScore;
    private int teamBScore;
    public FantasyWeekForTableDTO from(FantasyWeek fantasyWeek) {
        this.weekNumber = fantasyWeek.getWeekNumber();
        this.fantasyTeamA = fantasyWeek.getFantasyTeamA().getTeamName();
        this.fantasyTeamB = fantasyWeek.getFantasyTeamB().getTeamName();
        this.teamAScore = fantasyWeek.getTeamAScore();
        this.teamBScore = fantasyWeek.getTeamBScore();
        return this;
    }
}
