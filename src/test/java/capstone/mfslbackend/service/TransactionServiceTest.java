package capstone.mfslbackend.service;

import capstone.mfslbackend.repository.FantasyTeamRepository;
import capstone.mfslbackend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private FantasyTeamService fantasyTeamService;
    @Mock
    private PlayerService playerService;
    @Mock
    private FantasyLeagueService fantasyLeagueService;
    private final TransactionService transactionService = new TransactionService(transactionRepository, fantasyTeamService, playerService, fantasyLeagueService);


    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(transactionService, "transactionRepository", transactionRepository);
        ReflectionTestUtils.setField(transactionService, "fantasyTeamService", fantasyTeamService);
        ReflectionTestUtils.setField(transactionService, "playerService", playerService);
        ReflectionTestUtils.setField(transactionService, "fantasyLeagueService", fantasyLeagueService);
    }

}
