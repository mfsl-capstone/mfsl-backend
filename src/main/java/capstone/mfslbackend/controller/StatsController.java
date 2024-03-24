package capstone.mfslbackend.controller;

import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.PlayerGameStats;
import capstone.mfslbackend.service.PlayerGameStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/stats")
public class StatsController {
    private final PlayerGameStatsService playerGameStatsService;
    StatsController(PlayerGameStatsService playerGameStatsService) {
        this.playerGameStatsService = playerGameStatsService;
    }
    @PostMapping("")
    public ResponseEntity<List<PlayerGameStats>> createGameStats(@RequestParam String fixtureId) throws Error404 {
        return playerGameStatsService.createPlayerGameStats(fixtureId);
    }

    @GetMapping("{id}")
    public ResponseEntity<PlayerGameStats> getGameStats(@PathVariable Long id) {
        PlayerGameStats playerGameStats = playerGameStatsService.getPlayerGameStatsById(id);
        return ResponseEntity.ok(playerGameStats);
    }
    @GetMapping("/player")
    public ResponseEntity<List<PlayerGameStats>> getPlayerGameStats(@RequestParam Long playerId) {
        List<PlayerGameStats> playerGameStats = playerGameStatsService.getPlayerGameStatsByPlayerId(playerId);
        if (playerGameStats.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(playerGameStats);
    }
}
