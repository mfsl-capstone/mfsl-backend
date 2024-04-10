package capstone.mfslbackend.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class FantasyWeekForMatchupDTO {
    private int weekNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private FantasyTeamWithNoTransactionsNoLeagueDTO fantasyTeamA;
    private FantasyTeamWithNoTransactionsNoLeagueDTO fantasyTeamB;
    private List<FantasyWeekPlayer> teamAPlayers;
    private List<FantasyWeekPlayer> teamBPlayers;
}
