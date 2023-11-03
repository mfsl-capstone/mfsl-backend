package capstone.mfslbackend.controller;

import capstone.mfslbackend.model.Team;
import capstone.mfslbackend.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class TeamController {
    private final TeamService teamService;
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/team")
    public ResponseEntity<Team> getTeam(@RequestParam long teamId) {
        Optional<Team> team = teamService.getTeamById(teamId);
        return team.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/teams")
    public ResponseEntity<List<Team>> getTeams() {
        List<Team> teams = teamService.getAllTeams();
        if (CollectionUtils.isEmpty(teams)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(teams);
    }
    @PostMapping("/team")
    public ResponseEntity<Team> createTeam(@RequestParam String teamId) {
        Team team = teamService.createTeamById(teamId);
        if (team == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(team);
    }
    @PostMapping("/team/create-league")
    public ResponseEntity<List<Team>> createTeamByLeague(@RequestParam String leagueId,
                                                         @RequestParam String season) {
        return teamService.createTeamsInLeague(leagueId, season);
    }
}