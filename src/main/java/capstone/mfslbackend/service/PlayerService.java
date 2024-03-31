package capstone.mfslbackend.service;

import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.error.Error500;
import capstone.mfslbackend.model.Game;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.PlayerGameStats;
import capstone.mfslbackend.model.Team;
import capstone.mfslbackend.repository.PlayerRepository;
import capstone.mfslbackend.response.container.PlayersContainer;
import capstone.mfslbackend.response.dto.PlayerResponse;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map;

@Service
public class PlayerService {
    private final String baseUrl;
    private final PlayerRepository playerRepository;
    private final TeamService teamService;
    private final ApiService apiService;
    private final GameService gameService;

    public PlayerService(PlayerRepository playerRepository, TeamService teamService, ApiService apiService,
                         @Value("${base.url}") String baseUrl, GameService gameService) {
        this.playerRepository = playerRepository;
        this.teamService = teamService;
        this.apiService = apiService;
        this.baseUrl = baseUrl;
        this.gameService = gameService;
    }

    public ResponseEntity<List<Player>> createAllPlayersForAllTeams() {
        List<Player> players = new ArrayList<>();
        List<Team> teams = teamService.getAllTeams();
        teams.forEach(team -> players.addAll(createAllPlayersForTeam(team.getTeamId()).stream().toList()));
        return ResponseEntity.ok(players);
    }

    public List<Player> createAllPlayersForTeam(Long teamId) throws Error404, Error500 {
        PlayersContainer playersContainer;
        try {
            URL url = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/players/squads")
                    .queryParam("team", teamId)
                    .build().toUri().toURL();
            playersContainer = apiService.getRequest(url, PlayersContainer.class);
        } catch (Exception e) {
            throw new Error500("Error creating players for team: " + teamId);
        }
        if (playersContainer == null || CollectionUtils.isEmpty(playersContainer.getResponse())
                || CollectionUtils.isEmpty(playersContainer.getResponse().get(0).getPlayers())) {
            throw new Error404("No players found for team: " + teamId);
        }
        Team team = teamService.getTeamById(teamId);
        team.setPlayers(new ArrayList<>());
        return playersContainer.getResponse().get(0).getPlayers().stream()
                .map(playerResponse -> createPlayer(playerResponse, team))
                .filter(Objects::nonNull)
                .toList();
    }

    public Player createPlayer(PlayerResponse playerResponse, Team team) throws Error404 {

        Player player;
        try {
            player = getPlayerById(playerResponse.getId());
        } catch (Error404 e) {
            player = new Player(
                    playerResponse.getId(),
                    playerResponse.getName(),
                    playerResponse.getPosition(),
                    playerResponse.getPhoto(),
                    playerResponse.getNumber(),
                    0,
                    team, null);
            playerRepository.save(player);
            return player;
        }
        if (player.getPosition() == null || !player.getPosition().equals(playerResponse.getPosition())) {
            player.setPosition(playerResponse.getPosition());
        }
        if (playerResponse.getNumber() != null && !playerResponse.getNumber().equals(player.getNumber())) {
            player.setNumber(playerResponse.getNumber());
        }
        if (player.getTeam() == null || !player.getTeam().equals(team)) {
            player.setTeam(team);
        }

        return player;
    }

    public Player getPlayerById(Long playerId) throws Error404 {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new Error404("Player with id " + playerId + " not found"));
    }

    public List<Player> getPlayers(List<Player> players, List<Map<String, String>> filters, String sortDirection, String sortField, Boolean noTaken, int limit, int offset) {

        Specification<Player> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (noTaken && !CollectionUtils.isEmpty(players)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.not(root.in(players)));
            }

//          this might need changing later depending on what filters we want to implement (for example search by real life team/league)
            if (!CollectionUtils.isEmpty(filters)) {
                for (Map<String, String> filter : filters) {
                    String field = filter.get("field");
                    String value = filter.get("value");
                    String[] values = value.split(",");
                    List<Predicate> orPredicates = new ArrayList<>();
                    for (String val : values) {
                        if (field.equals("teamId")) {
                            orPredicates.add(criteriaBuilder.equal(root.get("team").get("teamId"), Long.parseLong(val)));
                        } else if (field.equals("teamName")) {
                            orPredicates.add(criteriaBuilder.like(root.get("team").get("name"), "%" + val + "%"));
                        } else if (root.get(field).getJavaType() == String.class) {
                            orPredicates.add(criteriaBuilder.like(root.get(field), "%" + val + "%"));
                        } else {
                            orPredicates.add(criteriaBuilder.equal(root.get(field), val));
                        }
                    }
                    Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
                    predicate = criteriaBuilder.and(predicate, orPredicate);
                }
            }

            return predicate;
        };

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Page<Player> playerPage = playerRepository.findAll(spec, PageRequest.of(offset, limit, sort));

        return playerPage.toList();
    }

    public Player createPlayerById(Long playerId) {
        PlayersContainer playersContainer;
        try {
            URL url = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/players/squads")
                    .queryParam("player", playerId)
                    .build().toUri().toURL();
            playersContainer = apiService.getRequest(url, PlayersContainer.class);
        } catch (Exception e) {
            throw new Error500("Error creating player:" + playerId);
        }
        if (playersContainer == null || CollectionUtils.isEmpty(playersContainer.getResponse())) {
            throw new Error404("No players found for player: " + playerId);
        }
        return createPlayer(playersContainer.getResponse().get(0).getPlayers().get(0), null);
    }

    public List<Game> getFutureGamesForPlayer(Long playerId) {
        Player player = getPlayerById(playerId);
        LocalDate now = LocalDate.now();
        return gameService.getGamesBetweenDates(now, now.plusYears(1)).stream()
                .filter(game -> game.getHomeTeam().equals(player.getTeam()) || game.getAwayTeam().equals(player.getTeam()))
                .toList();
    }

    public List<PlayerGameStats> getPlayerGameStats(Long playerId) {
        return getPlayerById(playerId).getPlayerGameStats();
    }

    public Player getRandomPlayer() {
        int randomIndex = (int) (Math.random() * playerRepository.count());
        return playerRepository.findAll(PageRequest.of(randomIndex, 1)).toList().get(0);
    }
    public void fixPlayerPoints() {
        List<Player> players = playerRepository.findAll();
        players.forEach(player -> {
            int points = player.getPlayerGameStats().stream().map(PlayerGameStats::getPoints).reduce(0, Integer::sum);
            player.setPoints(points);
            playerRepository.save(player);
        });
    }
}
