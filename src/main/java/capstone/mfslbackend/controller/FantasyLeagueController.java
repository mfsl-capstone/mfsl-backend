package capstone.mfslbackend.controller;

import capstone.mfslbackend.model.FantasyLeague;
import capstone.mfslbackend.service.FantasyLeagueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
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
    @GetMapping("{league-name}")
    public ResponseEntity<List<FantasyLeague>> getFantasyLeagueByName(@PathVariable String fantasyTeamName) {
        List<FantasyLeague> fantasyLeagues = fantasyLeagueService.getFantasyLeagueByName(fantasyTeamName);
        return ResponseEntity.ok(fantasyLeagues);
    }
    @PostMapping("create-league")
    public ResponseEntity<FantasyLeague> createFantasyLeague(@RequestParam String leagueName) {
        FantasyLeague fantasyLeague = fantasyLeagueService.createFantasyLeague(leagueName);
        return ResponseEntity.ok(fantasyLeague);
    }
}