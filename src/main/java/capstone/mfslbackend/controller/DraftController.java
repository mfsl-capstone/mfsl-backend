package capstone.mfslbackend.controller;

import capstone.mfslbackend.model.Draft;
import capstone.mfslbackend.model.Transaction;
import capstone.mfslbackend.service.DraftService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/draft")
public class DraftController {
    private final DraftService draftService;
    public DraftController(DraftService draftService) {
        this.draftService = draftService;
    }

    @GetMapping("{fantasyLeagueId}")
    public ResponseEntity<Draft> getDraft(@PathVariable long fantasyLeagueId) {
        Draft league = draftService.getDraft(fantasyLeagueId);
        return ResponseEntity.ok(league);
    }

    @PostMapping("{fantasyLeagueId}")
    public ResponseEntity<Transaction> draftPlayer(@PathVariable long fantasyLeagueId, @RequestParam long fantasyTeamId,
                                                   @RequestParam long playerId) {
        Transaction transaction = draftService.draftPlayer(fantasyLeagueId, fantasyTeamId, playerId);
        return ResponseEntity.ok(transaction);
    }
}
