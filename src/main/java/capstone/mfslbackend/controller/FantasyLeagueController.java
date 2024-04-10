package capstone.mfslbackend.controller;

import capstone.mfslbackend.DTO.FantasyLeaguePlayer;
import capstone.mfslbackend.DTO.FantasyWeeksDTO;
import capstone.mfslbackend.DTO.StandingPlayerDTO;
import capstone.mfslbackend.error.Error400;
import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.FantasyLeague;
import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.model.FantasyWeek;
import capstone.mfslbackend.service.FantasyLeagueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@RestController()
@RequestMapping("/fantasy-league")
public class FantasyLeagueController {
    private final FantasyLeagueService fantasyLeagueService;
    public FantasyLeagueController(FantasyLeagueService fantasyLeagueService) {
        this.fantasyLeagueService = fantasyLeagueService;
    }
    @GetMapping("")
    public ResponseEntity<FantasyLeague> getFantasyLeague(@RequestParam Long fantasyLeagueId) throws Error404 {
        FantasyLeague fantasyLeague = fantasyLeagueService.getFantasyLeagueById(fantasyLeagueId);
        return ResponseEntity.ok(fantasyLeague);
    }
    @GetMapping("{fantasyLeagueName}")
    public ResponseEntity<List<FantasyLeague>> getFantasyLeagueByName(@PathVariable String fantasyLeagueName) {
        List<FantasyLeague> fantasyLeagues = fantasyLeagueService.getFantasyLeagueByName(fantasyLeagueName);
        return ResponseEntity.ok(fantasyLeagues);
    }
    @PostMapping("")
    public ResponseEntity<FantasyLeague> createFantasyLeague(@RequestParam String leagueName, @RequestParam LocalDateTime draftDate) {
        FantasyLeague fantasyLeague = fantasyLeagueService.createFantasyLeague(leagueName, draftDate);
        return ResponseEntity.ok(fantasyLeague);
    }

    @PostMapping("join-league")
    public ResponseEntity<FantasyLeague> joinFantasyLeague(@RequestParam String username, @RequestParam Long leagueId, @RequestParam String leagueName, @RequestParam String teamName, @RequestParam String jerseyColour) throws Error400 {
        FantasyLeague fantasyLeague = fantasyLeagueService.joinFantasyLeague(username, leagueId, leagueName, teamName, jerseyColour);
        return ResponseEntity.ok(fantasyLeague);
    }

    @PostMapping("players")
    public ResponseEntity<List<FantasyLeaguePlayer>> getFantasyLeaguePlayers(@RequestParam Long leagueId,
                                                                             @RequestParam(required = false, defaultValue = "false") Boolean noTaken,
                                                                             @RequestParam(required = false, defaultValue = "desc") String sortDirection,
                                                                             @RequestParam(required = false, defaultValue = "playerId") String sortField,
                                                                             @RequestParam(required = false, defaultValue = "100") int limit,
                                                                             @RequestParam(required = false, defaultValue = "0") int offset,
                                                                             @RequestBody(required = false) List<Map<String, String>> filters) throws Error404 {
        List<FantasyLeaguePlayer> players = fantasyLeagueService.getFantasyLeaguePlayers(leagueId, filters, sortDirection, sortField, noTaken, limit, offset);
        return ResponseEntity.ok(players);
    }

    @GetMapping("matchups")
    public ResponseEntity<List<FantasyWeek>> getFantasyLeagueMatchups(@RequestParam Long leagueId, @RequestParam int weekNumber) throws Error404 {
        List<FantasyWeek> matchups = fantasyLeagueService.getFantasyLeagueMatchups(leagueId, weekNumber);
        return ResponseEntity.ok(matchups);
    }

    @GetMapping("weeks")
    public ResponseEntity<List<FantasyWeek>> getFantasyLeagueWeeks(@RequestParam Long leagueId) throws Error404 {
        List<FantasyWeek> weeks = fantasyLeagueService.getFantasyWeeksByLeagueId(leagueId);
        return ResponseEntity.ok(weeks);
    }

    @PostMapping("matchups")
    public ResponseEntity<List<FantasyWeek>> createFantasyLeagueMatchups(@RequestParam Long leagueId) {
        List<FantasyWeek> fantasyWeeks = fantasyLeagueService.createFantasyLeagueSchedule(leagueId);
        return ResponseEntity.ok(fantasyWeeks);
    }

    @GetMapping("completed-weeks")
    public ResponseEntity<FantasyWeeksDTO> getCompletedFantasyWeeks(@RequestParam Long leagueId) {
        List<FantasyWeek> weeks = fantasyLeagueService.getCompletedFantasyWeeks(leagueId);
        return ResponseEntity.ok(new FantasyWeeksDTO().from(weeks));
    }

    @GetMapping("incomplete-weeks")
    public ResponseEntity<FantasyWeeksDTO> getIncompleteFantasyWeeks(@RequestParam Long leagueId) {
        List<FantasyWeek> weeks = fantasyLeagueService.getIncompleteFantasyWeeks(leagueId);
        return ResponseEntity.ok(new FantasyWeeksDTO().from(weeks));
    }

    @GetMapping("results")
    public ResponseEntity<List<StandingPlayerDTO>> getFantasyLeagueResults(@RequestParam Long leagueId, @RequestParam String sortField, @RequestParam String sortDirection) {
        List<FantasyTeam> teams = fantasyLeagueService.getFantasyLeagueResults(leagueId, sortField, sortDirection);
        return ResponseEntity.ok(new StandingPlayerDTO().from(teams));
    }

}
