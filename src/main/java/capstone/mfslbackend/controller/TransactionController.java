package capstone.mfslbackend.controller;


import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.Transaction;
import capstone.mfslbackend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RestController()
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    @PostMapping()
    public ResponseEntity<Transaction> createTransaction(@RequestParam Long fantasyTeamId, @RequestParam Long incomingPlayerId, @RequestParam Long outgoingPlayerId) {
        Transaction transaction = transactionService.createTransaction(fantasyTeamId, incomingPlayerId, outgoingPlayerId);
        return ResponseEntity.ok(transaction);
    }
    @PostMapping("draft")
    public ResponseEntity<Transaction> createDraftTransaction(@RequestParam Long fantasyTeamId, @RequestParam Long incomingPlayerId) {
        Transaction transaction = transactionService.draftTransaction(fantasyTeamId, incomingPlayerId);
        return ResponseEntity.ok(transaction);
    }
    @GetMapping("{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("accept")
    public ResponseEntity<Transaction> acceptTransaction(@RequestParam Long transactionId) {
        Transaction transaction = transactionService.acceptTransaction(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("reject")
    public ResponseEntity<Transaction> rejectTransaction(@RequestParam Long transactionId) {
        Transaction transaction = transactionService.rejectTransaction(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("isValid")
    public ResponseEntity<List<Player>> isValidTransaction(@RequestParam Long fantasyTeamId, @RequestParam Long incomingPlayerId) {
        List<Player> isValid = transactionService.isValid(fantasyTeamId, incomingPlayerId);
        return ResponseEntity.ok(isValid);
    }

}
