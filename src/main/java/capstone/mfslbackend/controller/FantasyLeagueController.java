package capstone.mfslbackend.controller;

import capstone.mfslbackend.DTO.FantasyLeaguePlayer;
import capstone.mfslbackend.model.FantasyLeague;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.service.FantasyLeagueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController()
@RequestMapping("/fantasy-league")
public class FantasyLeagueController {
    private final FantasyLeagueService fantasyLeagueService;
    public FantasyLeagueController(FantasyLeagueService fantasyLeagueService) {
        this.fantasyLeagueService = fantasyLeagueService;
    }
    @GetMapping("")
    public ResponseEntity<FantasyLeague> getFantasyLeague(@RequestParam Long fantasyLeagueId) {
        Optional<FantasyLeague> fantasyLeague = fantasyLeagueService.getFantasyLeagueById(fantasyLeagueId);
        return fantasyLeague.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("{fantasyLeagueName}")
    public ResponseEntity<List<FantasyLeague>> getFantasyLeagueByName(@PathVariable String fantasyLeagueName) {
        List<FantasyLeague> fantasyLeagues = fantasyLeagueService.getFantasyLeagueByName(fantasyLeagueName);
        return ResponseEntity.ok(fantasyLeagues);
    }
    @PostMapping("create-league")
    public ResponseEntity<FantasyLeague> createFantasyLeague(@RequestParam String leagueName) {
        FantasyLeague fantasyLeague = fantasyLeagueService.createFantasyLeague(leagueName);
        return ResponseEntity.ok(fantasyLeague);
    }

    @PostMapping("join-league")
    public ResponseEntity<FantasyLeague> joinFantasyLeague(@RequestParam String username, @RequestParam Long leagueId, @RequestParam String teamName) {
        FantasyLeague fantasyLeague = fantasyLeagueService.joinFantasyLeague(username, leagueId, teamName);
        if (fantasyLeague == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fantasyLeague);
    }

    @GetMapping("players")
    public ResponseEntity<List<FantasyLeaguePlayer>> getFantasyLeaguePlayers(@RequestParam Long leagueId,
                                                                             @RequestParam(required = false, defaultValue = "false") Boolean noTaken,
                                                                             @RequestParam(required = false, defaultValue = "desc") String sortDirection,
                                                                             @RequestParam(required = false, defaultValue = "playerId") String sortField,
                                                                             @RequestParam(required = false, defaultValue = "100") int limit,
                                                                             @RequestParam(required = false, defaultValue = "0") int offset,
                                                                             @RequestBody(required = false) List<Map<String, String>> filters) {
        List<FantasyLeaguePlayer> players = fantasyLeagueService.getFantasyLeaguePlayers(leagueId, filters, sortDirection, sortField, noTaken, limit, offset);
        return ResponseEntity.ok(players);
    }
}
