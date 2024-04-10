package capstone.mfslbackend.controller;

import capstone.mfslbackend.DTO.DraftDTO;
import capstone.mfslbackend.DTO.TransactionDTO;
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

import java.time.LocalDateTime;

@RestController()
@RequestMapping("draft")
public class DraftController {
    private final DraftService draftService;
    public DraftController(DraftService draftService) {
        this.draftService = draftService;
    }

    @GetMapping("{fantasyLeagueId}")
    public ResponseEntity<DraftDTO> getDraft(@PathVariable long fantasyLeagueId) {
        Draft draft = draftService.getDraft(fantasyLeagueId);
        return ResponseEntity.ok(new DraftDTO().from(draft));
    }
    @GetMapping("status")
    public ResponseEntity<String> getDraftStatus(@RequestParam long fantasyLeagueId) {
        String draft = draftService.getDraftStatus(fantasyLeagueId);
        return ResponseEntity.ok(draft);
    }
    @GetMapping("date")
    public ResponseEntity<LocalDateTime> getDraftDate(@RequestParam long fantasyLeagueId) {
        LocalDateTime draft = draftService.getDraftDate(fantasyLeagueId);
        return ResponseEntity.ok(draft);
    }
    @PostMapping("")
    public ResponseEntity<TransactionDTO> draftPlayer(@RequestParam long fantasyTeamId,
                                                      @RequestParam long playerId) {
        Transaction transaction = draftService.draftPlayer(fantasyTeamId, playerId);
        return ResponseEntity.ok(new TransactionDTO().from(transaction));
    }
}
