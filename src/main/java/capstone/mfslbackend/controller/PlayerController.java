package capstone.mfslbackend.controller;

import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    @GetMapping()
    public ResponseEntity<List<Player>> getPlayers(@RequestParam(required = false, defaultValue = "desc") String sortDirection,
                                                   @RequestParam(required = false, defaultValue = "playerId") String sortField,
                                                   @RequestParam(required = false, defaultValue = "100") int limit,
                                                   @RequestParam(required = false, defaultValue = "0") int offset,
                                                   @RequestBody(required = false) List<Map<String, String>> filters) {
        List<Player> players = playerService.getPlayers(null, filters, sortDirection, sortField, false, limit, offset);
        if (CollectionUtils.isEmpty(players)) {
            return ResponseEntity.notFound().build();
        }
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
}
