package capstone.mfslbackend.controller;

import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.PlayerGameStats;
import capstone.mfslbackend.service.PlayerGameStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {
    private final PlayerGameStatsService playerGameStatsService;
    StatsController(PlayerGameStatsService playerGameStatsService) {
        this.playerGameStatsService = playerGameStatsService;
    }
    @PostMapping("{fixtureId}")
    public ResponseEntity<List<PlayerGameStats>> createGameStats(@PathVariable String fixtureId) throws Error404 {
        return ResponseEntity.ok(playerGameStatsService.createPlayerGameStats(fixtureId));
    }
    @PostMapping()
    public ResponseEntity<List<PlayerGameStats>> createGameStatsBetweenDate(@RequestParam LocalDate start,
                                                                            @RequestParam LocalDate end) {
        return ResponseEntity.ok(playerGameStatsService.createAllPlayerGameStatsBetweenDates(start, end));
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
