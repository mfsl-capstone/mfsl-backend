package capstone.mfslbackend.controller;

import capstone.mfslbackend.model.Team;
import capstone.mfslbackend.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController()
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }


    @GetMapping("{teamId}")
    @Secured({"LEAGUE_MEMBER", "LEAGUE_ADMIN"})
    public ResponseEntity<Team> getTeam(@PathVariable long teamId) {
        Team team = teamService.getTeamById(teamId);
        return ResponseEntity.ok(team);
    }
    @GetMapping()
    public ResponseEntity<List<Team>> getTeams() {
        List<Team> teams = teamService.getAllTeams();
        if (CollectionUtils.isEmpty(teams)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(teams);
    }
    @PostMapping("{teamId}")
    public ResponseEntity<Team> createTeam(@PathVariable Long teamId) {
        Team team = teamService.createTeamById(teamId);
        if (team == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(team);
    }
    @PostMapping("create-league")
    public ResponseEntity<List<Team>> createTeamByLeague(@RequestParam String leagueId,
                                                         @RequestParam String season) {
        List<Team> teams = teamService.createTeamsInLeague(leagueId, season);
        return ResponseEntity.ok(teams);
    }

}
