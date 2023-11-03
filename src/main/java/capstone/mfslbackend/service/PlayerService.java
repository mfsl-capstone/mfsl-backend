package capstone.mfslbackend.service;

import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.Team;
import capstone.mfslbackend.repository.PlayerRepository;
import capstone.mfslbackend.response.container.PlayersContainer;
import capstone.mfslbackend.response.dto.PlayerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class PlayerService {
    private final String baseUrl;
    private final PlayerRepository playerRepository;
    private final TeamService teamService;
    private final ApiService apiService;

    public PlayerService(PlayerRepository playerRepository, TeamService teamService, ApiService apiService,
                         @Value("${base.url}") String baseUrl) {
        this.playerRepository = playerRepository;
        this.teamService = teamService;
        this.apiService = apiService;
        this.baseUrl = baseUrl;
    }

    public ResponseEntity<List<Player>> createAllPlayersForAllTeams() {
        List<Player> players = new ArrayList<>();
        List<Team> teams = teamService.getAllTeams();
        teams.forEach(team -> players.addAll(createAllPlayersForTeam(team.getTeamId()).stream().toList()));
        return ResponseEntity.ok(players);
    }

    public List<Player> createAllPlayersForTeam(Long teamId) {
        PlayersContainer playersContainer;
        try {
            URL url = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/players/squads")
                    .queryParam("team", teamId)
                    .build().toUri().toURL();
            playersContainer = apiService.getRequest(url, PlayersContainer.class);
        } catch (Exception e) {
            log.error("error getting squad for team {}", teamId, e);
            return null;
        }
        if (playersContainer == null || CollectionUtils.isEmpty(playersContainer.getResponse()) ||
                CollectionUtils.isEmpty(playersContainer.getResponse().get(0).getPlayers())) {
            log.error("empty squad found for team {}", teamId);
            return null;
        }
        log.warn(playersContainer.toString());
        return playersContainer.getResponse().get(0).getPlayers().stream()
                .map(playerResponse -> createPlayer(playerResponse, teamId))
                .filter(Objects::nonNull)
                .toList();
    }

    public Player createPlayer(PlayerResponse playerResponse, Long teamId) {
        Optional<Team> t = teamService.getTeamById(teamId);
        if (t.isEmpty()) {
            log.error("Team {} does not exist", teamId);
            return null;
        }
        Team team = t.get();
        Optional<Player> p = getPlayerById(playerResponse.getId());
        if (p.isPresent()) {
            Player player = p.get();

            if (!player.getPosition().equals(playerResponse.getPosition()))
                updatePlayerPositionById(playerResponse.getId(), playerResponse.getPosition());

            if (player.getNumber()!=null && !player.getNumber().equals(playerResponse.getNumber()))
                updatePlayerNumberById(playerResponse.getId(), playerResponse.getNumber());

            if (!player.getTeam().getTeamId().equals(teamId)){
                player.setTeam(team);
                playerRepository.save(player);
            }
            return player;
        }
        Player player = new Player();
        player.setPlayerId(playerResponse.getId());
        player.setName(playerResponse.getName());
        player.setUrl(playerResponse.getPhoto());
        player.setPosition(playerResponse.getPosition());
        player.setNumber(playerResponse.getNumber());
        player.setTeam(team);
        playerRepository.save(player);
        return player;
    }

    public Optional<Player> getPlayerById(Long playerId) {
        Optional<Player> player = playerRepository.findById(playerId);
        if (player.isEmpty()) log.warn("no player with id {} found", playerId);
        return player;
    }

    public void updatePlayerNumberById(Long playerId, Integer number) {
        if (number == null) return;
        playerRepository.updatePlayerNumberById(playerId, number);
    }
    public void updatePlayerPositionById(Long playerId, String position) {
        if (position.isEmpty()) return;
        playerRepository.updatePlayerPositionById(playerId, position);
    }
}
