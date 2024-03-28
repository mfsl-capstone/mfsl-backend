package capstone.mfslbackend.service;

import capstone.mfslbackend.model.FantasyLeague;
import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.Transaction;
import capstone.mfslbackend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.mockito.Mockito.lenient;

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
