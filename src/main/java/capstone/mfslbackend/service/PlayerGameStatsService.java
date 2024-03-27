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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.time.LocalDate;
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
    @Value("${YELLOW.CARD.POINTS}")
    private int yellowCardPoints;
    @Value("${RED.CARD.POINTS}")
    private int redCardPoints;
    @Value("${MINUTES.POINTS}")
    private int minutesPoints;
    @Value("${MINUTES.THRESHOLD}")
    private int minutesThreshold;
    @Value("${PENALTY.COMMITTED.POINTS}")
    private int penaltyCommittedPoints;
    @Value("${PENALTY.MISSED.POINTS}")
    private int penaltyMissedPoints;
    @Value("${SAVES.POINTS}")
    private int savesPoints;
    @Value("${SAVES.THRESHOLD}")
    private int savesThreshold;
    @Value("${GOALS.CONCEDED.THRESHOLD}")
    private int goalsConcededThreshold;
    @Value("${PENALTIES.SAVED.POINTS}")
    private int penaltiesSavedPoints;
    @Value("${GK.CLEAN.SHEET.POINTS}")
    private int gkCleanSheetPoints;
    @Value("${DEF.CLEAN.SHEET.POINTS}")
    private int defCleanSheetPoints;
    @Value("${MID.CLEAN.SHEET.POINTS}")
    private int midCleanSheetPoints;
    @Value("${GK.GOALS.SCORED.POINTS}")
    private int gkGoalsScoredPoints;
    @Value("${GK.ASSISTS.POINTS}")
    private int gkAssistsPoints;
    @Value("${DEF.GOALS.SCORED.POINTS}")
    private int defGoalsScoredPoints;
    @Value("${MID.GOALS.SCORED.POINTS}")
    private int midGoalsScoredPoints;
    @Value("${ATT.GOALS.SCORED.POINTS}")
    private int attGoalsScoredPoints;
    @Value("${DEF.ASSISTS.POINTS}")
    private int defAssistsPoints;
    @Value("${MID.ASSISTS.POINTS}")
    private int midAssistsPoints;
    @Value("${ATT.ASSISTS.POINTS}")
    private int attAssistsPoints;
    @Value("${SHOT.ACCURACY.THRESHOLD}")
    private int shotAccuracyThreshold;
    @Value("${RATING.POINTS}")
    private int ratingPoints;
    @Value("${FRACTION.TO.PERCENT}")
    private int fractionToPercent;
    @Value("${RATING.THRESHOLD.1}")
    private int ratingThreshold1;
    @Value("${RATING.THRESHOLD.2}")
    private double ratingThreshold2;
    @Value("${RATING.THRESHOLD.3}")
    private int ratingThreshold3;
    @Value("${RATING.THRESHOLD.4}")
    private int ratingThreshold4;


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
    public List<PlayerGameStats> createAllPlayerGameStatsBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<PlayerGameStats> playerGameStats = new ArrayList<>();
        List<Game> games = gameService.getGamesBetweenDates(startDate, endDate);
        games.forEach(game -> {
            try {
                playerGameStats.addAll(createPlayerGameStats(game.getId().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return playerGameStats;
    }

    public List<PlayerGameStats> createPlayerGameStats(String fixtureId) {
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
                Player p;
                try {
                    p = playerService.getPlayerById(players.getPlayer().getId());
                } catch (Error404 e) {
                    p = playerService.createPlayerById(players.getPlayer().getId());
                }
                convert(players.getStatistics().get(0), statsResponse2.getResponse().get(0).getLeague().getRound(), score, opp, g, p, response.getTeam())
                            .ifPresent(playerGameStats::add);
                p.setPoints(p.getPlayerGameStats().stream().map(PlayerGameStats::getPoints).reduce(0, Integer::sum));
            }
        }
        return playerGameStats;
    }

    private Optional<PlayerGameStats> convert(StatisticResponse stat, String round, String score, String opp, Game game, Player player, TeamResponse team) {
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
                playerGameStats.setRound(Integer.parseInt(matcher.group()));
            }
        }
        playerGameStats.setScore(score);
        playerGameStats.setOpp(opp);
        boolean cleanSheet = false;
        if (game != null) {
            playerGameStats.setGame(game);
            if (game.getAwayTeamScore() == 0  && team.getId() == game.getHomeTeam().getTeamId()) {
                cleanSheet = true;
            }
            if (game.getHomeTeamScore() == 0 && team.getId() == game.getAwayTeam().getTeamId()) {
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
        playerGameStats.setCleanSheet(cleanSheet);
        points -= playerGameStats.getYellowCards();
        playerGameStats.setYellowCardPoints(playerGameStats.getYellowCards() * yellowCardPoints);
        points += playerGameStats.getRedCards() * redCardPoints;
        playerGameStats.setRedCardPoints(playerGameStats.getRedCards() * redCardPoints);
        points += playerGameStats.getMinutes() / minutesThreshold + minutesPoints;
        playerGameStats.setMinutesPoints((playerGameStats.getMinutes() / minutesThreshold) + minutesPoints);
        if (playerGameStats.getMinutes() == 0) {
            playerGameStats.setMinutesPoints(0);
            return 0;
        }
        points += playerGameStats.getPenaltiesCommitted() * penaltyCommittedPoints;
        playerGameStats.setPenaltyCommittedPoints(playerGameStats.getPenaltiesCommitted() * penaltyCommittedPoints);
        points += playerGameStats.getPenaltiesMissed() * penaltyMissedPoints;
        playerGameStats.setPenaltiesMissedPoints(playerGameStats.getPenaltiesMissed() * penaltyMissedPoints);
        switch (playerGameStats.getPlayer().getPosition()) {
            case "Goalkeeper" -> {
                points += (playerGameStats.getSaves() / savesThreshold) * savesPoints;
                playerGameStats.setSavesPoints((playerGameStats.getSaves() / savesThreshold * savesPoints));
                points -= (playerGameStats.getGoalsConceded() / goalsConcededThreshold);
                playerGameStats.setGoalsConcededPoints(-playerGameStats.getGoalsConceded() / goalsConcededThreshold);
                points += playerGameStats.getPenaltiesSaved() * penaltiesSavedPoints;
                playerGameStats.setPenaltiesSavedPoints(playerGameStats.getPenaltiesSaved() * penaltiesSavedPoints);
                if (playerGameStats.getMinutes() > minutesThreshold) {
                    points += cleanSheet ? gkCleanSheetPoints : 0;
                    playerGameStats.setCleanSheetPoints(cleanSheet ? gkCleanSheetPoints : 0);
                }
                points += playerGameStats.getGoalsScored() * gkGoalsScoredPoints;
                playerGameStats.setGoalsScoredPoints(playerGameStats.getGoalsScored() * gkGoalsScoredPoints);
                points += playerGameStats.getAssists() * gkAssistsPoints;
                playerGameStats.setAssistsPoints(playerGameStats.getAssists() * gkAssistsPoints);
            }
            case "Defender" -> {
                points += playerGameStats.getSaves();
                playerGameStats.setSavesPoints(playerGameStats.getSaves());
                if (playerGameStats.getMinutes() > minutesThreshold) {
                    points += cleanSheet ? defCleanSheetPoints : 0;
                    playerGameStats.setCleanSheetPoints(cleanSheet ? defCleanSheetPoints : 0);
                }
                points += playerGameStats.getGoalsScored() * defGoalsScoredPoints;
                playerGameStats.setGoalsScoredPoints(playerGameStats.getGoalsScored() * defGoalsScoredPoints);
                points += playerGameStats.getAssists() * defAssistsPoints;
                playerGameStats.setAssistsPoints(playerGameStats.getAssists() * defAssistsPoints);
            }
            case "Midfielder" -> {
                points += playerGameStats.getGoalsScored() * midGoalsScoredPoints;
                playerGameStats.setGoalsScoredPoints(playerGameStats.getGoalsScored() * midGoalsScoredPoints);
                points += playerGameStats.getAssists() * midAssistsPoints;
                playerGameStats.setAssistsPoints(playerGameStats.getAssists() * midAssistsPoints);
                if (playerGameStats.getMinutes() > minutesThreshold) {
                    points += cleanSheet ? midCleanSheetPoints : 0;
                    playerGameStats.setCleanSheetPoints(cleanSheet ? midCleanSheetPoints : 0);
                }
            }
            case "Attacker" -> {
                points += playerGameStats.getGoalsScored() * attGoalsScoredPoints;
                playerGameStats.setGoalsScoredPoints(playerGameStats.getGoalsScored() * attGoalsScoredPoints);
                points += playerGameStats.getAssists() * attAssistsPoints;
                playerGameStats.setAssistsPoints(playerGameStats.getAssists() * attAssistsPoints);
                if (playerGameStats.getShotsTaken() > 0) {
                    playerGameStats.setShotAccuracy((playerGameStats.getShotsOnTarget() / playerGameStats.getShotsTaken()) * fractionToPercent);
                    points += playerGameStats.getShotAccuracy() / shotAccuracyThreshold;
                    playerGameStats.setShotAccuracyPoints(playerGameStats.getShotAccuracy() / shotAccuracyThreshold);
                }
            }
            default -> {
                return 0;
            }
        }
        if (playerGameStats.getRating() >= ratingThreshold1) {
            points += ratingPoints;
            playerGameStats.setRatingPoints(playerGameStats.getRatingPoints() + ratingPoints);
        }
        if (playerGameStats.getRating() >= ratingThreshold2) {
            points += ratingPoints;
            playerGameStats.setRatingPoints(playerGameStats.getRatingPoints() + ratingPoints);
        }
        if (playerGameStats.getRating() >= ratingThreshold3) {
            points += ratingPoints;
            playerGameStats.setRatingPoints(playerGameStats.getRatingPoints() + ratingPoints);
        }
        if (playerGameStats.getRating() < ratingThreshold4) {
            points -= ratingPoints;
            playerGameStats.setRatingPoints(playerGameStats.getRatingPoints() - ratingPoints);
        }
        return points;
    }
}
