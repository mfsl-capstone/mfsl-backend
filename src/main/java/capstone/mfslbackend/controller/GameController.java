package capstone.mfslbackend.controller;

import capstone.mfslbackend.model.Game;
import capstone.mfslbackend.response.dto.GamesResponse;
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
@RequestMapping("/player")
public class GameController {
    private final GameService gameService;
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("get-game")
    public ResponseEntity<Game> getGame(@RequestParam Long gameId) {
        Optional<Game> game = gameService.getGamebyID(gameId);
        return game.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping("create-game")
    public ResponseEntity<Game> createGame(@RequestParam GamesResponse gamesResponse) {
       Game game = gameService.createGame(gamesResponse);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(game);
    }
    @GetMapping("get-games-by-round")
    public ResponseEntity<List<Game>> getGamesByRound(@RequestParam String round) {
        List<Game> games = gameService.getGamesByRound(round);
        if (CollectionUtils.isEmpty(games)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(games);
    }
}
