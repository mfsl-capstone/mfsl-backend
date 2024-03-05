package capstone.mfslbackend.controller;

import capstone.mfslbackend.DTO.FantasyTeamLineup;
import capstone.mfslbackend.error.Error400;
import capstone.mfslbackend.error.Error404;
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
    public ResponseEntity<FantasyTeam> getFantasyTeam(@PathVariable long fantasyTeamId) throws Error404 {
        FantasyTeam team = fantasyTeamService.getFantasyTeam(fantasyTeamId);
        return ResponseEntity.ok(team);
    }

    @GetMapping("lineup/{fantasyTeamId}")
    public ResponseEntity<FantasyTeamLineup> getFantasyTeamLineup(@PathVariable long fantasyTeamId) throws Error404 {
        return ResponseEntity.ok(fantasyTeamService.getFantasyTeamLineup(fantasyTeamId));
    }
    @PostMapping("lineup/{fantasyTeamId}")
    public ResponseEntity<FantasyTeamLineup> setFantasyTeamLineup(@PathVariable long fantasyTeamId, @RequestParam String lineup) throws Error404, Error400 {
        return ResponseEntity.ok(fantasyTeamService.setFantasyTeamLineup(fantasyTeamId, lineup));
    }
}
