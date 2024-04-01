package capstone.mfslbackend.DTO;

import capstone.mfslbackend.model.FantasyLeague;
import lombok.Data;

import java.util.Comparator;
import java.util.List;

@Data
public class FantasyLeagueDTO {
    private long id;
    private String leagueName;
    private List<FantasyTeamWithNoTransactionsNoLeagueDTO> fantasyTeams;
    public FantasyLeagueDTO from(FantasyLeague fantasyLeague) {
        this.id = fantasyLeague.getId();
        this.leagueName = fantasyLeague.getLeagueName();
        this.fantasyTeams = fantasyLeague.getFantasyTeams().stream()
                .map(t -> new FantasyTeamWithNoTransactionsNoLeagueDTO().from(t))
                .sorted(Comparator.comparingInt(FantasyTeamWithNoTransactionsNoLeagueDTO::getOrderNumber))
                .toList();
        return this;
    }
}
