package capstone.mfslbackend.controller;

import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.service.FantasyTeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.Optional;

@RestController()
@RequestMapping("/fantasy-team")
public class FantasyTeamController {

    private final FantasyTeamService fantasyTeamService;
    public FantasyTeamController(FantasyTeamService fantasyTeamService) {
        this.fantasyTeamService = fantasyTeamService;
    }

    @GetMapping("{fantasyTeamId}")
    public ResponseEntity<FantasyTeam> getFantasyTeam(@PathVariable long fantasyTeamId) {
        Optional<FantasyTeam> team = fantasyTeamService.getFantasyTeam(fantasyTeamId);
        return team.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<FantasyTeam> createFantasyTeam(@RequestParam String teamName) {
       FantasyTeam fantasyTeam = fantasyTeamService.createFantasyTeam(teamName);
        if (fantasyTeam == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fantasyTeam);

    }
}
