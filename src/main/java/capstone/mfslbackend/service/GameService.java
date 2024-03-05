package capstone.mfslbackend.service;

import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.Game;
import capstone.mfslbackend.model.Team;
import capstone.mfslbackend.repository.GameRepository;
import capstone.mfslbackend.response.container.GamesContainer;
import capstone.mfslbackend.response.dto.GamesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GameService {
    private final String baseUrl;
    private final GameRepository gameRepository;
    private final ApiService apiService;
    private final TeamService teamService;
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
            log.error("error getting all games for league: {} and season: {}", leagueId, season, e);
            throw new Error404("Could not find all games for league: " + leagueId + " and season: " + season);
        }
        if (gamesContainer == null || CollectionUtils.isEmpty(gamesContainer.getResponse())) {
            log.error("Could not find any games for league: {} and season: {}", leagueId, season);
            throw new Error404("Could not find any games for league: " + leagueId + " and season: " + season);
        }
        return ResponseEntity.ok(gamesContainer.getResponse().stream()
                .map(this::createGame)
                .toList());
    }

    public Game createGame(GamesResponse gamesResponse) {
        Optional<Team> h = teamService.getTeamById(gamesResponse.getTeams().getHome().getId());
        Optional<Team> a = teamService.getTeamById(gamesResponse.getTeams().getAway().getId());
        Team home = h.orElseGet(() -> teamService.createTeamById(gamesResponse.getTeams().getHome().getId()));
        Team away = a.orElseGet(() -> teamService.createTeamById(gamesResponse.getTeams().getAway().getId()));

        Game game = new Game();
        game.setId(gamesResponse.getFixture().getId());
        game.setDate(gamesResponse.getFixture().getDate());
        game.setRound(gamesResponse.getLeague().getRound());

        gameRepository.save(game);

        teamService.addGameToTeam(home.getTeamId(), game);
        teamService.addGameToTeam(away.getTeamId(), game);

        return game;
    }


    public Game getGameById(long gameId) throws Error404 {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new Error404("Could not find game with id " + gameId));
    }

    public List<Game> getGamesByRound(String round) throws Error404 {
        List<Game> allGames = gameRepository.findAll();
        List<Game> filteredGames = allGames.stream().filter(game -> game.getRound().equalsIgnoreCase(round)).toList();
        if (CollectionUtils.isEmpty(filteredGames)) {
            log.warn("could not find any games for round {}", round);
            throw new Error404("Could not find any games for round " + round);
        }
        return filteredGames;
    }


}
