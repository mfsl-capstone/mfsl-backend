package capstone.mfslbackend.service;

import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.Game;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.PlayerGameStats;
import capstone.mfslbackend.repository.PlayerGameStatsRepository;
import capstone.mfslbackend.response.container.StatsContainer;
import capstone.mfslbackend.response.dto.PlayerStatsResponse;
import capstone.mfslbackend.response.dto.PlayersStatsResponse;
import capstone.mfslbackend.response.dto.TeamResponse;
import capstone.mfslbackend.response.dto.stats.StatisticResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PlayerGameStatsService {

    private final ApiService apiService;
    private final String baseUrl;
    private final PlayerService playerService;
    private final PlayerGameStatsRepository playerGameStatsRepository;
    private final GameService gameService;

    public PlayerGameStatsService(ApiService apiService, PlayerGameStatsRepository playerGameStatsRepository,
                                  PlayerService playerService, @Value("${base.url}") String baseUrl,
                                  GameService gameService) {
        this.apiService = apiService;
        this.baseUrl = baseUrl;
        this.playerService = playerService;
        this.playerGameStatsRepository = playerGameStatsRepository;
        this.gameService = gameService;
    }

    public PlayerGameStats getPlayerGameStatsById(Long id) {
        return playerGameStatsRepository.findById(id)
                .orElseThrow(() -> new Error404("Could not find player game stats with id: " + id));
    }

    public List<PlayerGameStats> getPlayerGameStatsByPlayerId(Long playerId) {
        Player player = playerService.getPlayerById(playerId);
        return playerGameStatsRepository.findByPlayer(player);
    }
    public ResponseEntity<List<PlayerGameStats>> createPlayerGameStats(String fixtureId) {
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
            throw new Error404("Could not find fixture: " + fixtureId);
        }

        if (statsResponse == null || CollectionUtils.isEmpty(statsResponse.getResponse())
                || statsResponse2 == null || CollectionUtils.isEmpty(statsResponse2.getResponse())) {
            throw new Error404("Could not find fixture: " + fixtureId);
        }
        Game g = gameService.getGameById(Long.parseLong(fixtureId));
        g.setHomeTeamScore(statsResponse2.getResponse().get(0).getGoals().getHome());
        g.setAwayTeamScore(statsResponse2.getResponse().get(0).getGoals().getAway());

        for (PlayersStatsResponse response: statsResponse.getResponse()) {
            TeamResponse home = statsResponse2.getResponse().get(0).getTeams().getHome();
            TeamResponse away = statsResponse2.getResponse().get(0).getTeams().getAway();
            String score = statsResponse2.getResponse().get(0).getGoals().getHome()
                    + "-"
                    + statsResponse2.getResponse().get(0).getGoals().getAway();
            String opp = "";
            if (!home.equals(response.getTeam())) {
                if (statsResponse2.getResponse().get(0).getGoals().getAway() - statsResponse2.getResponse().get(0).getGoals().getHome() > 0) {
                    score += " W";
                } else if (statsResponse2.getResponse().get(0).getGoals().getAway() - statsResponse2.getResponse().get(0).getGoals().getHome() < 0) {
                    score += " L";
                } else {
                    score += " D";
                }
                opp = home.getName() + " (A)";
            } else {
                if (statsResponse2.getResponse().get(0).getGoals().getHome() - statsResponse2.getResponse().get(0).getGoals().getAway() > 0) {
                    score += " W";
                } else if (statsResponse2.getResponse().get(0).getGoals().getHome() - statsResponse2.getResponse().get(0).getGoals().getAway() < 0) {
                    score += " L";
                } else {
                    score += " D";
                }
                opp = away.getName() + " (H)";
            }

            for (PlayerStatsResponse players : response.getPlayers()) {
                try {
                    playerService.getPlayerById(players.getPlayer().getId());
                } catch (Error404 e) {
                    playerService.createPlayerById(players.getPlayer().getId(), statsResponse2.getResponse().get(0).getLeague().getSeason());
                }
                convert(players.getStatistics().get(0), statsResponse2.getResponse().get(0).getLeague().getRound(), score, opp, g, playerService.getPlayerById(players.getPlayer().getId()))
                            .ifPresent(playerGameStats::add);
            }
        }
        return ResponseEntity.ok(playerGameStats);
    }

    private Optional<PlayerGameStats> convert(StatisticResponse stat, String round, String score, String opp, Game game, Player player) {
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
            playerGameStats.setSaves(playerGameStats.getSaves()  + Optional.ofNullable(stat.getTackles().getBlocks()).orElse(0));
            playerGameStats.setInterceptions(Optional.ofNullable(stat.getTackles().getInterceptions()).orElse(0));
        }
        if (!round.isEmpty()) {
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(round);
            if (matcher.find()) {
                playerGameStats.setRound(Integer.parseInt(matcher.group()));;
            }
        }
        playerGameStats.setScore(score);
        playerGameStats.setOpp(opp);
        boolean cleanSheet = false;
        if (game != null) {
            playerGameStats.setGame(game);
            if (game.getAwayTeamScore() == 0  && player.getTeam().equals(game.getHomeTeam())) {
                cleanSheet = true;
            }
            if (game.getHomeTeamScore() == 0 && player.getTeam().equals(game.getAwayTeam())) {
                cleanSheet = true;
            }
        }

        playerGameStats.setPlayer(player);
        playerGameStats.setPoints(calculatePoints(playerGameStats, cleanSheet));
        playerGameStatsRepository.save(playerGameStats);
        return Optional.of(playerGameStats);
    }

    private int calculatePoints(PlayerGameStats playerGameStats, boolean cleanSheet) {
        int points = 0;
        points -= playerGameStats.getYellowCards();
        playerGameStats.setYellowCardPoints(playerGameStats.getYellowCards() * -1);
        points -= playerGameStats.getRedCards() * 4;
        playerGameStats.setRedCardPoints(playerGameStats.getRedCards() * -4);
        points += playerGameStats.getMinutes() / 60 + 1;
        playerGameStats.setMinutesPoints((playerGameStats.getMinutes() / 60) + 1);
        if (playerGameStats.getMinutes() == 0) {
            playerGameStats.setMinutesPoints(0);
            return 0;
        }
        points -= playerGameStats.getPenaltiesCommitted() * 4;
        playerGameStats.setPenaltyCommittedPoints(playerGameStats.getPenaltiesCommitted() * -4);
        points -= playerGameStats.getFoulsCommitted() / 2;
        playerGameStats.setFoulsCommittedPoints(playerGameStats.getFoulsCommitted() / -2);
        points -= playerGameStats.getPenaltiesMissed() * 4;
        playerGameStats.setPenaltiesMissedPoints(playerGameStats.getPenaltiesMissed() * -4);
        switch (playerGameStats.getPlayer().getPosition()) {
            case "Goalkeeper" -> {
                points += (playerGameStats.getSaves() / 3) * 2;
                playerGameStats.setSavesPoints((playerGameStats.getSaves() / 3) * 2);
                points -= (playerGameStats.getGoalsConceded() / 2);
                playerGameStats.setGoalsConcededPoints((playerGameStats.getGoalsConceded() / -2));
                points += playerGameStats.getPenaltiesSaved() * 5;
                playerGameStats.setPenaltiesSavedPoints(playerGameStats.getPenaltiesSaved() * 5);
                if (playerGameStats.getMinutes() > 60) {
                    points += cleanSheet ? 6 : 0;
                    playerGameStats.setCleanSheetPoints(cleanSheet ? 6 : 0);
                }
                points += playerGameStats.getGoalsScored() * 10;
                playerGameStats.setGoalsScoredPoints(playerGameStats.getGoalsScored() * 10);
                points += playerGameStats.getAssists() * 7;
                playerGameStats.setAssistsPoints(playerGameStats.getAssists() * 7);
            }
            case "Defender" -> {
                points += playerGameStats.getSaves();
                playerGameStats.setSavesPoints(playerGameStats.getSaves());
                if (playerGameStats.getMinutes() > 60) {
                    points += cleanSheet ? 4 : 0;
                    playerGameStats.setCleanSheetPoints(cleanSheet ? 4 : 0);
                }
                points += playerGameStats.getGoalsScored() * 6;
                playerGameStats.setGoalsScoredPoints(playerGameStats.getGoalsScored() * 6);
                points += playerGameStats.getAssists() * 4;
                playerGameStats.setAssistsPoints(playerGameStats.getAssists() * 4);
            }
            case "Midfielder" -> {
                points += playerGameStats.getGoalsScored() * 4;
                playerGameStats.setGoalsScoredPoints(playerGameStats.getGoalsScored() * 4);
                points += playerGameStats.getAssists() * 3;
                playerGameStats.setAssistsPoints(playerGameStats.getAssists() * 3);
                if (playerGameStats.getMinutes() > 60) {
                    points += cleanSheet ? 2 : 0;
                    playerGameStats.setCleanSheetPoints(cleanSheet ? 2 : 0);
                }            }
            case "Attacker" -> {
                points += playerGameStats.getGoalsScored() * 4;
                playerGameStats.setGoalsScoredPoints(playerGameStats.getGoalsScored() * 4);
                points += playerGameStats.getAssists() * 3;
                playerGameStats.setAssistsPoints(playerGameStats.getAssists() * 3);
                playerGameStats.setShotAccuracy(0);
                if (playerGameStats.getShotsTaken() > 0) {
                    points += (playerGameStats.getShotsOnTarget() / playerGameStats.getShotsTaken()) / .7;
                    playerGameStats.setShotAccuracy((playerGameStats.getShotsOnTarget() / playerGameStats.getShotsTaken()) * 100);
                    playerGameStats.setShotAccuracyPoints(playerGameStats.getShotAccuracy() / 70);
                }
            }
            default -> {
                return 0;
            }
        }
        playerGameStats.setRatingPoints(0);
        if (playerGameStats.getRating() >= 8) {
            points += 1;
            playerGameStats.setRatingPoints(1);
        }
        if (playerGameStats.getRating() >= 8.5) {
            points += 1;
            playerGameStats.setRatingPoints(2);
        }
        if (playerGameStats.getRating() >= 9) {
            points += 1;
            playerGameStats.setRatingPoints(3);
        }
        if (playerGameStats.getRating() < 5) {
            points -= 1;
            playerGameStats.setRatingPoints(-1);
        }
        return points;
    }
}
