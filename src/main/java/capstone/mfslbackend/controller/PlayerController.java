package capstone.mfslbackend.controller;

import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/player")
public class PlayerController {
    private final PlayerService playerService;
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("")
    public ResponseEntity<Player> getPlayer(@RequestParam Long playerId) {
        Player player = playerService.getPlayerById(playerId);
        return ResponseEntity.ok(player);
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
}
