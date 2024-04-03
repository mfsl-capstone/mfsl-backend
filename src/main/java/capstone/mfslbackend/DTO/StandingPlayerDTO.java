package capstone.mfslbackend.DTO;

import capstone.mfslbackend.model.FantasyTeam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandingPlayerDTO {
    private FantasyTeamWithNoTransactionsNoLeagueDTO team;
    private int rank;
    public List<StandingPlayerDTO> from(List<FantasyTeam> teams) {
        List<FantasyTeam> teamsCopy = new ArrayList<>(teams);
        teamsCopy.sort(Comparator.comparingInt(FantasyTeam::getPoints)
                .thenComparingInt(FantasyTeam::getFantasyPoints)
                .thenComparingInt(FantasyTeam::getWins));
        List<StandingPlayerDTO> standingPlayerDTOS = new ArrayList<>();
        for (FantasyTeam team : teams) {
            standingPlayerDTOS.add(new StandingPlayerDTO(new FantasyTeamWithNoTransactionsNoLeagueDTO().from(team), teamsCopy.indexOf(team) + 1));
        }
        return standingPlayerDTOS;
    }
}
