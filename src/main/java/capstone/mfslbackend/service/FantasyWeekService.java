package capstone.mfslbackend.service;

import capstone.mfslbackend.error.Error400;
import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.FantasyLeague;
import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.model.FantasyWeek;
import capstone.mfslbackend.model.Game;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.PlayerGameStats;
import capstone.mfslbackend.model.enums.FantasyWeekStatus;
import capstone.mfslbackend.repository.FantasyWeekRepository;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;


@Service
public class FantasyWeekService {

    private final FantasyWeekRepository fantasyWeekRepository;
    private final FantasyLeagueService fantasyLeagueService;
    private final PlayerGameStatsService playerGameStatsService;
    private final PlayerService playerService;
    private static final int GAME_DURATION = 4;
    private static final int MIN_GK = 1;
    private static final int MIN_DEF = 3;
    private static final int MAX_DEF = 5;
    private static final int MIN_MID = 3;
    private static final int MAX_MID = 5;
    private static final int MIN_FWD = 1;
    private static final int MAX_FWD = 3;
    private static final int DEFAULT_LIMIT = 100;
    private static final int STARTING_XI = 11;
    private static final int MAX_HOUR = 23;
    private static final int MAX_MINUTE_SECOND = 59;

    public FantasyWeekService(FantasyWeekRepository fantasyWeekRepository, FantasyLeagueService fantasyLeagueService,
                              PlayerGameStatsService playerGameStatsService, PlayerService playerService) {
        this.fantasyWeekRepository = fantasyWeekRepository;
        this.fantasyLeagueService = fantasyLeagueService;
        this.playerGameStatsService = playerGameStatsService;
        this.playerService = playerService;
    }

    public FantasyWeek getFantasyWeekById(Long weekId) throws Error404 {
        return fantasyWeekRepository.findById(weekId)
                .orElseThrow(() -> new Error404("could not find week with id " + weekId));
    }

    public FantasyWeek getFantasyWeekByWeekNumber(long fantasyLeagueId, long fantasyTeamId, int weekNumber) {
        FantasyLeague league = fantasyLeagueService.getFantasyLeagueById(fantasyLeagueId);
        FantasyTeam team = league.getFantasyTeams().stream()
                .filter(fantasyTeam -> fantasyTeam.getId().equals(fantasyTeamId))
                .findFirst()
                .orElseThrow(() -> new Error404("Could not find team with id " + fantasyTeamId + " in league with id " + fantasyLeagueId));
        return team.getFantasyWeeks().stream()
                .filter(fantasyWeek -> fantasyWeek.getWeekNumber() == weekNumber)
                .findFirst()
                .orElseThrow(() -> new Error404("Team with id " + fantasyTeamId + " does not have a week with number " + weekNumber));
    }
    public FantasyWeek getActiveFantasyWeek(long fantasyLeagueId, long fantasyTeamId) {
        return getFantasyWeekByDate(fantasyLeagueId, fantasyTeamId, LocalDate.now());
    }
    public FantasyWeek getFantasyWeekByDate(long fantasyLeagueId, long fantasyTeamId, LocalDate date) {
        FantasyLeague league = fantasyLeagueService.getFantasyLeagueById(fantasyLeagueId);
        FantasyTeam team = league.getFantasyTeams().stream()
                .filter(fantasyTeam -> fantasyTeam.getId().equals(fantasyTeamId))
                .findFirst()
                .orElseThrow(() -> new Error404("Could not find team with id " + fantasyTeamId + " in league with id " + fantasyLeagueId));
        return team.getFantasyWeeks().stream()
                .filter(fantasyWeek -> fantasyWeek.getStartDate().isBefore(LocalDate.now()) && fantasyWeek.getEndDate().isAfter(LocalDate.now()))
                .findFirst()
                .orElseThrow(() -> new Error404("Team with id " + fantasyTeamId + " does not have a week for the date: " + date));
    }
    public List<FantasyWeek> getFantasyWeeksByLeagueId(long fantasyLeagueId) {
        FantasyLeague league = fantasyLeagueService.getFantasyLeagueById(fantasyLeagueId);
        return league.getFantasyTeams().stream()
                .flatMap(fantasyTeam -> fantasyTeam.getFantasyWeeks().stream())
                .toList();
    }
    public void startActiveFantasyWeeks() {
        Specification<FantasyWeek> spec = (root, query, criteriaBuilder) -> {
            Predicate date = criteriaBuilder.lessThan(root.get("startDate"), LocalDate.now());
            Predicate status = criteriaBuilder.equal(root.get("status"), FantasyWeekStatus.NOT_STARTED);
            return criteriaBuilder.and(date, status);
        };
        List<FantasyWeek> fantasyWeeks = fantasyWeekRepository.findAll(spec);
        fantasyWeeks.forEach(fantasyWeek -> {
            fantasyWeek.setStatus(FantasyWeekStatus.IN_PROGRESS);
            fantasyWeek.setTeamAInOrder(fantasyWeek.getFantasyTeamA().getPlayerIdsInOrder());
            fantasyWeek.setTeamBInOrder(fantasyWeek.getFantasyTeamB().getPlayerIdsInOrder());
        });
    }
    public void updateActiveFantasyWeeks() {
        Specification<FantasyWeek> spec = (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("status"), FantasyWeekStatus.IN_PROGRESS);
        List<FantasyWeek> fantasyWeeks = fantasyWeekRepository.findAll(spec);

        fantasyWeeks.forEach(fantasyWeek -> {
            String playerIds = fantasyWeek.getFantasyTeamA().getPlayerIdsInOrder().replace(" ", ",") + "," + fantasyWeek.getFantasyTeamB().getPlayerIdsInOrder().replace(" ", ",");
            List<Player> players = playerService.getPlayers(null, List.of(Map.of("field", "playerId", "value", playerIds)), "asc", "playerId", false, DEFAULT_LIMIT, 0);

            updateGamesInFantasyWeek(players);
            setFantasyWeekStats(fantasyWeek, players);
            finishFantasyWeek(fantasyWeek, players);
        });
    }

    public void updateGamesInFantasyWeek(List<Player> players) {
        players.stream().map(player -> player.getTeam().getGames().stream()
                        .filter(game -> game.getDate().isBefore(LocalDateTime.now().minusHours(GAME_DURATION))
                                && CollectionUtils.isEmpty(game.getPlayerGameStats()))
                        .toList())
                .flatMap(List::stream)
                .map(Game::getId)
                .distinct()
                .forEach(gameId -> playerGameStatsService.createPlayerGameStats(String.valueOf(gameId)));
    }

    public void finishFantasyWeek(FantasyWeek fantasyWeek, List<Player> players) {
        if (fantasyWeek.getStatus() != FantasyWeekStatus.IN_PROGRESS || fantasyWeek.getEndDate().isAfter(LocalDate.now())) {
            return;
        }
        fantasyWeek.setStatus(FantasyWeekStatus.COMPLETED);
        List<Player> playersTeamA  = Stream.of(fantasyWeek.getFantasyTeamA().getPlayerIdsInOrder().split(" "))
                .map(Long::parseLong)
                .map(playerId -> players.stream().filter(player -> player.getPlayerId().equals(playerId)).findFirst().orElse(null))
                .toList();
        List<Player> playersTeamB  = Stream.of(fantasyWeek.getFantasyTeamB().getPlayerIdsInOrder().split(" "))
                .map(Long::parseLong)
                .map(playerId -> players.stream().filter(player -> player.getPlayerId().equals(playerId)).findFirst().orElse(null))
                .toList();
        Map<Integer, String> teamA = calculateFantasyWeekScore(playersTeamA, fantasyWeek.getStatsTeamA());
        fantasyWeek.setTeamAScore(teamA.keySet().stream().findFirst().orElse(0));
        fantasyWeek.setTeamAInOrder(teamA.values().stream().findFirst().orElse(""));
        Map<Integer, String> teamB = calculateFantasyWeekScore(playersTeamB, fantasyWeek.getStatsTeamB());
        fantasyWeek.setTeamBScore(teamB.keySet().stream().findFirst().orElse(0));
        fantasyWeek.setTeamBInOrder(teamB.values().stream().findFirst().orElse(""));
    }

    private Map<Integer, String> calculateFantasyWeekScore(List<Player> players, Set<PlayerGameStats> stats) {
        int sum = 0;
        List<Player> startingXI = players.subList(0, STARTING_XI);
        List<Player> bench = players.subList(STARTING_XI, players.size());
        for (Player player : startingXI) {
            List<PlayerGameStats> playerStats = stats.stream()
                    .filter(playerGameStats -> playerGameStats.getPlayer().getPlayerId().equals(player.getPlayerId()))
                    .toList();
            if (playerStats.stream()
                    .map(PlayerGameStats::getMinutes)
                    .reduce(0, Integer::sum) > 0) {
                sum += playerStats.stream()
                        .map(PlayerGameStats::getMinutes)
                        .reduce(0, Integer::sum);
                continue;
            }
            for (Player benchPlayer: bench) {
                List<PlayerGameStats> benchPlayerStats = stats.stream()
                        .filter(playerGameStats -> playerGameStats.getPlayer().getPlayerId().equals(benchPlayer.getPlayerId()))
                        .toList();

                if (benchPlayerStats.stream()
                        .map(PlayerGameStats::getMinutes)
                        .reduce(0, Integer::sum) > 0
                        && isValidSwap(players.subList(0, STARTING_XI), player, benchPlayer)) {

                    sum += benchPlayerStats.stream()
                            .map(PlayerGameStats::getMinutes)
                            .reduce(0, Integer::sum);
                    startingXI.set(startingXI.indexOf(player), benchPlayer);
                    bench.set(bench.indexOf(benchPlayer), player);
                    break;
                }
            }
        }
        String teamInOrder = startingXI.stream().map(player -> player.getPlayerId().toString()).reduce("", (a, b) -> a + " " + b)
                + " "
                + bench.stream().map(player -> player.getPlayerId().toString()).reduce("", (a, b) -> a + " " + b);
        return Map.of(sum, teamInOrder);
    }
    private boolean isValidSwap(List<Player> players, Player playerOut, Player playerIn) {
        players.set(players.indexOf(playerOut), playerIn);
        int gkCount = 0;
        int defCount = 0;
        int midCount = 0;
        int fwdCount = 0;
        for (Player player : players) {
            switch (player.getPosition()) {
                case "Goalkeeper" -> gkCount++;
                case "Defender" -> defCount++;
                case "Midfielder" -> midCount++;
                case "Attacker" -> fwdCount++;
                default -> throw new Error400("Invalid position for player with id " + player.getPlayerId());
            }
        }
        return gkCount == MIN_GK && defCount >= MIN_DEF && defCount <= MAX_DEF && midCount >= MIN_MID && midCount <= MAX_MID && fwdCount >= MIN_FWD && fwdCount <= MAX_FWD;
    }
    private void setFantasyWeekStats(FantasyWeek fantasyWeek, List<Player> players) {
        List<Long> idsTeamA = Stream.of(fantasyWeek.getFantasyTeamA().getPlayerIdsInOrder().split(" ")).map(Long::parseLong).toList();
        List<Long> idsTeamB = Stream.of(fantasyWeek.getFantasyTeamB().getPlayerIdsInOrder().split(" ")).map(Long::parseLong).toList();
        List<PlayerGameStats> stats = players.stream().map(player -> player.getPlayerGameStats().stream()
                        .filter(stat -> stat.getGame().getDate().isAfter(fantasyWeek.getStartDate().atStartOfDay())
                                && stat.getGame().getDate().isBefore(fantasyWeek.getEndDate().atTime(MAX_HOUR, MAX_MINUTE_SECOND, MAX_MINUTE_SECOND)))
                        .toList())
                .flatMap(List::stream)
                .distinct()
                .toList();
        fantasyWeek.setStatsTeamA(new HashSet<>(stats.stream().filter(playerGameStats -> idsTeamA.contains(playerGameStats.getPlayer().getPlayerId())).toList()));
        fantasyWeek.setStatsTeamB(new HashSet<>(stats.stream().filter(playerGameStats -> idsTeamB.contains(playerGameStats.getPlayer().getPlayerId())).toList()));
    }

}
