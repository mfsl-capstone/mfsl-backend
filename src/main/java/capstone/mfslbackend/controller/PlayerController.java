package capstone.mfslbackend.controller;

import capstone.mfslbackend.model.Game;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.PlayerGameStats;
import capstone.mfslbackend.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/player")
public class PlayerController {
    private final PlayerService playerService;
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("{playerId}")
    public ResponseEntity<Player> getPlayer(@PathVariable Long playerId) {
        Player player = playerService.getPlayerById(playerId);
        return ResponseEntity.ok(player);
    }
    @PostMapping()
    public ResponseEntity<List<Player>> getPlayers(@RequestParam(required = false, defaultValue = "desc") String sortDirection,
                                                   @RequestParam(required = false, defaultValue = "playerId") String sortField,
                                                   @RequestParam(required = false, defaultValue = "100") int limit,
                                                   @RequestParam(required = false, defaultValue = "0") int offset,
                                                   @RequestBody(required = false) List<Map<String, String>> filters) {
        List<Player> players = playerService.getPlayers(null, filters, sortDirection, sortField, false, limit, offset);
        return ResponseEntity.ok(players);
    }
    @PostMapping("create-team")
    public ResponseEntity<List<Player>> createPlayersInTeam(@RequestParam Long teamId) {
        List<Player> players = playerService.createAllPlayersForTeam(teamId);
        return ResponseEntity.ok(players);
    }
    @PostMapping("create-all")
    public ResponseEntity<List<Player>> createAllPlayers() {
        return playerService.createAllPlayersForAllTeams();
    }

    @GetMapping("{playerId}/future-games")
    public ResponseEntity<List<Game>> getFutureGamesForPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(playerService.getFutureGamesForPlayer(playerId));
    }

    @GetMapping("{playerId}/game-stats")
    public ResponseEntity<List<PlayerGameStats>> getPlayerGameStats(@PathVariable Long playerId) {
        List<PlayerGameStats> playerGameStats = playerService.getPlayerGameStats(playerId);
        return ResponseEntity.ok(playerGameStats);
    }
}
