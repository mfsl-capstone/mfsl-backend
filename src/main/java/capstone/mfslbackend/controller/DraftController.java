package capstone.mfslbackend.controller;

import capstone.mfslbackend.model.Draft;
import capstone.mfslbackend.model.Transaction;
import capstone.mfslbackend.service.DraftService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
@RestController()
@RequestMapping("draft")
public class DraftController {
    private final DraftService draftService;
    public DraftController(DraftService draftService) {
        this.draftService = draftService;
    }

    @GetMapping("{fantasyLeagueId}")
    public ResponseEntity<Draft> getDraft(@PathVariable long fantasyLeagueId) {
        Draft draft = draftService.getDraft(fantasyLeagueId);
        return ResponseEntity.ok(draft);
    }

    @PostMapping("{fantasyLeagueId}")
    public ResponseEntity<Transaction> draftPlayer(@PathVariable long fantasyLeagueId, @RequestParam long fantasyTeamId,
                                                   @RequestParam long playerId) {
        Transaction transaction = draftService.draftPlayer(fantasyLeagueId, fantasyTeamId, playerId);
        return ResponseEntity.ok(transaction);
    }
}
