package capstone.mfslbackend.service;

import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.PlayerGameStats;
import capstone.mfslbackend.repository.PlayerGameStatsRepository;
import capstone.mfslbackend.response.container.StatsContainer;
import capstone.mfslbackend.response.dto.PlayerStatsResponse;
import capstone.mfslbackend.response.dto.PlayersStatsResponse;
import capstone.mfslbackend.response.dto.TeamResponse;
import capstone.mfslbackend.response.dto.stats.StatisticResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PlayerGameStatsService {

    private final ApiService apiService;
    private final String baseUrl;
    private final PlayerService playerService;
    private final PlayerGameStatsRepository playerGameStatsRepository;

    public PlayerGameStatsService(ApiService apiService, PlayerGameStatsRepository playerGameStatsRepository,
                                  PlayerService playerService, @Value("${base.url}") String baseUrl) {
        this.apiService = apiService;
        this.baseUrl = baseUrl;
        this.playerService = playerService;
        this.playerGameStatsRepository = playerGameStatsRepository;
    }

    public PlayerGameStats getPlayerGameStatsById(Long id) throws Error404 {
        return playerGameStatsRepository.findById(id)
                .orElseThrow(() ->new Error404("Could not find player game stats with id: " + id));
    }
    public ResponseEntity<List<PlayerGameStats>> createPlayerGameStats(String fixtureId) throws Error404 {
        List<PlayerGameStats> playerGameStats = new ArrayList<>();
        StatsContainer statsResponse;
        StatsContainer statsResponse2;
        try {
//            get individual player stats for a specific game
            URL url = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/fixtures/players")
                    .queryParam("fixture", fixtureId)
                    .build().toUri().toURL();
            statsResponse = apiService.getRequest(url, StatsContainer.class);

//            This request will be useful for determining the winner of the game
            url = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/fixtures")
                    .queryParam("id", fixtureId)
                    .build().toUri().toURL();
            statsResponse2 = apiService.getRequest(url, StatsContainer.class);
        } catch (Exception e) {
            log.error("error retrieving fixture {}", fixtureId, e);
            throw new Error404("Could not find fixture: " + fixtureId);
        }

        if (statsResponse == null || CollectionUtils.isEmpty(statsResponse.getResponse())
                || statsResponse2 == null || CollectionUtils.isEmpty(statsResponse2.getResponse())) {
            log.error("no results found for fixture {}", fixtureId);
            throw new Error404("Could not find fixture: " + fixtureId);
        }
        for (PlayersStatsResponse response: statsResponse.getResponse()) {
            TeamResponse home = statsResponse2.getResponse().get(0).getTeams().getHome();
            boolean winner = home.getWinner();
            if (!home.equals(response.getTeam())) {
                winner = !winner;
            }

            for (PlayerStatsResponse players : response.getPlayers()) {
                convert(players.getStatistics().get(0), statsResponse2.getResponse().get(0).getLeague().getRound(), winner)
                        .ifPresent(stats -> {
                            stats.setPlayer(playerService.getPlayerById(players.getPlayer().getId()).get());
                            playerGameStatsRepository.save(stats);
                            playerGameStats.add(stats);
                        });
            }
        }
        log.debug("all players from fixture {}: {}", fixtureId, playerGameStats);
        return ResponseEntity.ok(playerGameStats);
    }

    private Optional<PlayerGameStats> convert(StatisticResponse stat, String round, Boolean winner) {
        PlayerGameStats playerGameStats = new PlayerGameStats();

        if (stat == null) {
            return Optional.empty();
        }

        if (stat.getCards() != null) {
            playerGameStats.setYellowCards(Optional.ofNullable(stat.getCards().getYellow()).orElse(0));
            playerGameStats.setRedCards(Optional.ofNullable(stat.getCards().getRed()).orElse(0));
        }
        if (stat.getDribbles() != null) {
            playerGameStats.setSuccessfulDribbles(Optional.ofNullable(stat.getDribbles().getSuccess()).orElse(0));
        }
        if (stat.getDuels() != null) {
            playerGameStats.setDuelsWon(Optional.ofNullable(stat.getDuels().getWon()).orElse(0));
        }
        if (stat.getFouls() != null) {
            playerGameStats.setFoulsDrawn(Optional.ofNullable(stat.getFouls().getDrawn()).orElse(0));
            playerGameStats.setFoulsCommitted(Optional.ofNullable(stat.getFouls().getCommitted()).orElse(0));
        }
        if (stat.getGames() != null) {
            playerGameStats.setMinutes(Optional.ofNullable(stat.getGames().getMinutes()).orElse(0));
            playerGameStats.setRating(Optional.ofNullable(stat.getGames().getRating()).orElse((float) 0));
        }
        if (stat.getGoals() != null) {
            playerGameStats.setGoalsScored(Optional.ofNullable(stat.getGoals().getTotal()).orElse(0));
            playerGameStats.setGoalsConceded(Optional.ofNullable(stat.getGoals().getConceded()).orElse(0));
            playerGameStats.setAssists(Optional.ofNullable(stat.getGoals().getAssists()).orElse(0));
            playerGameStats.setSaves(Optional.ofNullable(stat.getGoals().getSaves()).orElse(0));
        }
        if (stat.getPasses() != null) {
            playerGameStats.setPasses(Optional.ofNullable(stat.getPasses().getTotal()).orElse(0));
            playerGameStats.setKeyPasses(Optional.ofNullable(stat.getPasses().getKey()).orElse(0));
            playerGameStats.setPassAccuracy(Optional.ofNullable(stat.getPasses().getAccuracy()).orElse("0"));
        }
        if (stat.getPenalty() != null) {
            playerGameStats.setPenaltiesCommitted(Optional.ofNullable(stat.getPenalty().getCommited()).orElse(0));
            playerGameStats.setPenaltiesScored(Optional.ofNullable(stat.getPenalty().getScored()).orElse(0));
            playerGameStats.setPenaltiesMissed(Optional.ofNullable(stat.getPenalty().getMissed()).orElse(0));
            playerGameStats.setPenaltiesSaved(Optional.ofNullable(stat.getPenalty().getSaved()).orElse(0));
        }
        if (stat.getShots() != null) {
            playerGameStats.setShotsTaken(Optional.ofNullable(stat.getShots().getTotal()).orElse(0));
            playerGameStats.setShotsOnTarget(Optional.ofNullable(stat.getShots().getOn()).orElse(0));
        }
        if (stat.getTackles() != null) {
            playerGameStats.setTackles(Optional.ofNullable(stat.getTackles().getTotal()).orElse(0));
            playerGameStats.setShotBlocks(Optional.ofNullable(stat.getTackles().getBlocks()).orElse(0));
            playerGameStats.setInterceptions(Optional.ofNullable(stat.getTackles().getInterceptions()).orElse(0));
        }
        if (!round.isEmpty()) {
            playerGameStats.setRound(round);
        }
        if (winner != null) {
            if (winner) {
                playerGameStats.setResult(1);
            } else {
                playerGameStats.setResult(0);
            }
        }

        return Optional.of(playerGameStats);
    }
}
