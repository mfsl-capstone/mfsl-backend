package capstone.mfslbackend.controller;

import capstone.mfslbackend.model.PlayerGameStats;
import capstone.mfslbackend.service.PlayerGameStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stats")
public class StatsController {
    private final PlayerGameStatsService playerGameStatsService;
    StatsController(PlayerGameStatsService playerGameStatsService) {
        this.playerGameStatsService = playerGameStatsService;
    }
    @PostMapping("")
    public ResponseEntity<List<PlayerGameStats>> createGameStats(@RequestParam String fixtureId) {
        return playerGameStatsService.createPlayerGameStats(fixtureId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerGameStats> getGameStats(@PathVariable Long id) {
        Optional<PlayerGameStats> playerOptional = playerGameStatsService.getPlayerGameStatsById(id);
        return playerOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
