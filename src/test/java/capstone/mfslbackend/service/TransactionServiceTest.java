package capstone.mfslbackend.service;

import capstone.mfslbackend.model.Transaction;
import capstone.mfslbackend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.lenient;

@SpringBootTest
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    private final TransactionService transactionService = new TransactionService(transactionRepository);

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(transactionService, "transactionRepository", transactionRepository);
    }

    @Test
    public void testCreateTransaction_Success() {
        // Arrange
        String date = "2021-01-01";
        lenient().when(transactionRepository.save(Mockito.any()))
                .thenReturn(new Transaction(1L, date));
        Transaction t = transactionService.createTransaction(date);
        assert (t.getDate().equals(date));
        assert(t.getId() == 1L);
    }
}
