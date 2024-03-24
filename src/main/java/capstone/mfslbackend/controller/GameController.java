package capstone.mfslbackend.controller;

import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.Game;
import capstone.mfslbackend.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("")
    public ResponseEntity<Game> getGame(@RequestParam Long gameId) throws Error404 {
        Game game = gameService.getGameById(gameId);
        return ResponseEntity.ok(game);
    }

    @PostMapping("create-games-league")
    public ResponseEntity<List<Game>> createGamesInLeague(@RequestParam String leagueId, @RequestParam String season) throws Error404 {
        return gameService.createAllGamesForLeague(leagueId, season);
    }

    @GetMapping("round")
    public ResponseEntity<List<Game>> getGamesByRound(@RequestParam String round) throws Error404 {
        List<Game> games = gameService.getGamesByRound(round);
        return ResponseEntity.ok(games);
    }

    @GetMapping("date")
    public ResponseEntity<List<Game>> getGamesByDate(@RequestParam LocalDate start,
                                                     @RequestParam LocalDate end) {
        List<Game> games = gameService.getGamesBetweenDates(start, end);
        return ResponseEntity.ok(games);
    }
}
