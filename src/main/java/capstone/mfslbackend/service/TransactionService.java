package capstone.mfslbackend.service;

import capstone.mfslbackend.model.Transaction;
import capstone.mfslbackend.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    public Transaction createTransaction(String date) {
        Transaction transaction = new Transaction();
        transaction.setDate(date);
        return transactionRepository.save(transaction);
    }
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
}
