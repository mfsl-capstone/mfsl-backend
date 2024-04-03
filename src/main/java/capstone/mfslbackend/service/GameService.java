package capstone.mfslbackend.service;

import capstone.mfslbackend.error.Error400;
import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.Game;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.Team;
import capstone.mfslbackend.repository.GameRepository;
import capstone.mfslbackend.response.container.GamesContainer;
import capstone.mfslbackend.response.dto.GamesResponse;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GameService {
    private final String baseUrl;
    private final GameRepository gameRepository;
    private final ApiService apiService;
    private final TeamService teamService;
    private static final int GAME_DURATION = 4;
    public GameService(GameRepository gameRepository, ApiService apiService, TeamService teamService,
                       @Value("${base.url}") String baseUrl) {
        this.gameRepository = gameRepository;
        this.apiService = apiService;
        this.teamService = teamService;
        this.baseUrl = baseUrl;
    }

    public ResponseEntity<List<Game>> createAllGamesForLeague(String leagueId, String season) throws Error404 {
        GamesContainer gamesContainer;
        try {
            URL url = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/fixtures")
                    .queryParam("league", leagueId)
                    .queryParam("season", season)
                    .build().toUri().toURL();
            gamesContainer = apiService.getRequest(url, GamesContainer.class);
        } catch (Exception e) {
            throw new Error404("Could not find all games for league: " + leagueId + " and season: " + season);
        }
        if (gamesContainer == null || CollectionUtils.isEmpty(gamesContainer.getResponse())) {
            throw new Error404("Could not find any games for league: " + leagueId + " and season: " + season);
        }
        return ResponseEntity.ok(gamesContainer.getResponse().stream()
                .map(this::createGame)
                .toList());
    }

    public Game createGame(GamesResponse gamesResponse) {
        Team home;
        try {
            home = teamService.getTeamById(gamesResponse.getTeams().getHome().getId());
        } catch (Error404 e) {
            home = teamService.createTeamById(gamesResponse.getTeams().getHome().getId());
        }
        Team away;
        try {
            away = teamService.getTeamById(gamesResponse.getTeams().getAway().getId());
        } catch (Error404 e) {
            away = teamService.createTeamById(gamesResponse.getTeams().getAway().getId());
        }

        Game game = new Game();
        game.setId(gamesResponse.getFixture().getId());
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        game.setDate(LocalDateTime.parse(gamesResponse.getFixture().getDate(), formatter));
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(gamesResponse.getLeague().getRound());
        if (matcher.find()) {
            game.setRound(Integer.parseInt(matcher.group()));
        }
        game.setHomeTeam(home);
        game.setAwayTeam(away);
        game.setHomeTeamScore(gamesResponse.getGoals().getHome());
        game.setAwayTeamScore(gamesResponse.getGoals().getAway());

        gameRepository.save(game);

        try {
            teamService.addGameToTeam(home, game);
            teamService.addGameToTeam(away, game);
        } catch (Error400 ignored) {
        }

        return game;
    }


    public Game getGameById(long gameId) throws Error404 {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new Error404("Could not find game with id " + gameId));
    }

    public List<Game> getGamesByRound(int round) throws Error404 {
        List<Game> allGames = gameRepository.findAll();
        List<Game> filteredGames = allGames.stream().filter(game -> game.getRound() == round).toList();
        if (CollectionUtils.isEmpty(filteredGames)) {
            throw new Error404("Could not find any games for round " + round);
        }
        return filteredGames;
    }

    public List<Game> getGamesBetweenDates(LocalDate start, LocalDate end) {
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);
        return gameRepository.findGamesByDateBetween(startTime, endTime);
    }

    public List<Game> getPastNoStatsGames() {
        Specification<Game> spec = (root, query, criteriaBuilder) -> {
            Predicate statsPredicate = criteriaBuilder.equal(root.get("playerGameStats"), null);
            Predicate pastPredicate = criteriaBuilder.lessThan(root.get("date"), LocalDateTime.now().minusHours(GAME_DURATION));
            return criteriaBuilder.and(statsPredicate, pastPredicate);
        };
        return gameRepository.findAll(spec);
    }

}
