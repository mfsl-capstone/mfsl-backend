package capstone.mfslbackend.controller;

import capstone.mfslbackend.model.FantasyTeam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/fantasy-team")
public class FantasyTeamController {

    @GetMapping("")
    public ResponseEntity<FantasyTeam> getFantasyTeam() {
//        todo
        return null;
    }

    @PostMapping("")
    public ResponseEntity<FantasyTeam> createFantasyTeam(@RequestParam String teamName) {
//        todo
        return null;
    }
}
