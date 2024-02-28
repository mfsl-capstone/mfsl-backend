package capstone.mfslbackend.controller;

import capstone.mfslbackend.model.Transaction;
import capstone.mfslbackend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@RestController()
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    @PostMapping()
    public void createTransaction(@RequestParam Long fantasyTeamId, @RequestParam Long incomingPlayerId, @RequestParam Long outgoingPlayerId) {
        transactionService.createTransaction(fantasyTeamId, incomingPlayerId, outgoingPlayerId);
    }
    @GetMapping("{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        return transaction.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
