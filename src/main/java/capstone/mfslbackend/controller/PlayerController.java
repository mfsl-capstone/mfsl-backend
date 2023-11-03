package capstone.mfslbackend.controller;

import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController()
public class PlayerController {
    private final PlayerService playerService;
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/player")
    public ResponseEntity<Player> getPlayer(@RequestParam Long playerId) {
        Optional<Player> player = playerService.getPlayerById(playerId);
        return player.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping("/player/create-team")
    public ResponseEntity<List<Player>> createPlayersInTeam(@RequestParam Long teamId) {
        List<Player> players = playerService.createAllPlayersForTeam(teamId);
        if (CollectionUtils.isEmpty(players)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(players);
    }
    @PostMapping("/player/create-all")
    public ResponseEntity<List<Player>> createAllPlayers() {
        return playerService.createAllPlayersForAllTeams();
    }
}
