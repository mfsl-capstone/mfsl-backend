package capstone.mfslbackend.controller;

import capstone.mfslbackend.model.Game;
import org.springframework.util.CollectionUtils;
import capstone.mfslbackend.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("")
    public ResponseEntity<Game> getGame(@RequestParam Long gameId) {
        Optional<Game> game = gameService.getGamebyID(gameId);
        return game.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("create-games-league")
    public ResponseEntity<List<Game>> createGamesInLeague(@RequestParam String leagueId, @RequestParam String season) {
        return gameService.createAllGamesForLeague(leagueId, season);
    }

    @GetMapping("round")
    public ResponseEntity<List<Game>> getGamesByRound(@RequestParam String round) {
        List<Game> games = gameService.getGamesByRound(round);
        if (CollectionUtils.isEmpty(games)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(games);
    }
}
