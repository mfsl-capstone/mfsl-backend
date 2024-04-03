package capstone.mfslbackend.factory;

import capstone.mfslbackend.DTO.FantasyTeamWithNoTransactionsNoLeagueDTO;
import capstone.mfslbackend.DTO.FantasyWeekForMatchupDTO;
import capstone.mfslbackend.DTO.FantasyWeekPlayer;
import capstone.mfslbackend.model.FantasyWeek;
import capstone.mfslbackend.model.Game;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.PlayerGameStats;
import capstone.mfslbackend.service.PlayerService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class FantasyWeekForMatchupDTOFactory {
    private final PlayerService playerService;
    private static final int HOURS_IN_DAY = 23;
    private static final int MINUTES_SECONDS = 59;
    public FantasyWeekForMatchupDTOFactory(PlayerService playerService) {
        this.playerService = playerService;
    }
    public FantasyWeekForMatchupDTO from(FantasyWeek fantasyWeek) {
        System.out.println(fantasyWeek);
        FantasyWeekForMatchupDTO fantasyWeekForMatchupDTO = new FantasyWeekForMatchupDTO();
        fantasyWeekForMatchupDTO.setWeekNumber(fantasyWeek.getWeekNumber());
        fantasyWeekForMatchupDTO.setStartDate(fantasyWeek.getStartDate());
        fantasyWeekForMatchupDTO.setEndDate(fantasyWeek.getEndDate());
        fantasyWeekForMatchupDTO.setFantasyTeamA(new FantasyTeamWithNoTransactionsNoLeagueDTO().from(fantasyWeek.getFantasyTeamA()));
        fantasyWeekForMatchupDTO.setFantasyTeamB(new FantasyTeamWithNoTransactionsNoLeagueDTO().from(fantasyWeek.getFantasyTeamB()));
        fantasyWeekForMatchupDTO.setTeamAPlayers(Stream.of(fantasyWeek.getTeamAInOrder().split(" "))
                .map(playerId -> {
                    Player p = fantasyWeek.getFantasyTeamA().getPlayers().stream()
                            .filter(player -> player.getPlayerId().equals(Long.parseLong(playerId)))
                            .findFirst().orElse(playerService.getPlayerById(Long.parseLong(playerId)));
                    List<PlayerGameStats> stats = fantasyWeek.getStatsTeamA().stream()
                            .filter(stat -> stat.getPlayer().getPlayerId().equals(Long.valueOf(playerId)))
                            .toList();
                    List<Game> games = p.getTeam().getGames().stream()
                            .filter(game -> game.getDate().isAfter(fantasyWeek.getStartDate().atStartOfDay()))
                            .filter(game -> game.getDate().isBefore(fantasyWeek.getEndDate().atTime(HOURS_IN_DAY, MINUTES_SECONDS)))
                            .filter(game -> stats.stream().noneMatch(stat -> stat.getGame().getId().equals(game.getId())))
                            .toList();
                    return new FantasyWeekPlayer().from(p, stats, games);
                })
                .toList());
        return fantasyWeekForMatchupDTO;
    }
}
