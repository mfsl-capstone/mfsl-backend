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

    @Test
    public void testCreateTransaction_Success() {
        // Arrange
        String date = "2021-01-01";
        FantasyLeague fantasyLeague = new FantasyLeague();
        fantasyLeague.setId(1L);
        Player player1 = new Player();
        player1.setPlayerId(1L);
        Player player2 = new Player();
        player2.setPlayerId(2L);
        FantasyTeam fantasyTeam = new FantasyTeam();
        fantasyTeam.setId(1L);
        fantasyTeam.setFantasyLeague(fantasyLeague);
        fantasyTeam.setPlayers(Set.of(player1));
        lenient().when(transactionRepository.save(Mockito.any(Transaction.class)))
                .thenAnswer(AdditionalAnswers.returnsFirstArg());
        lenient().when(fantasyTeamService.getFantasyTeam(Mockito.anyLong()))
                .thenReturn(java.util.Optional.of(fantasyTeam));

        lenient().when(fantasyLeagueService.getFantasyTeamOfTakenPlayer(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(java.util.Optional.empty());

        lenient().when(playerService.getPlayerById(Mockito.anyLong()))
                .thenReturn(java.util.Optional.of(player2));
        Transaction t = transactionService.createTransaction(1L, 2L, 1L);

//        dont check transaction id because it is auto generated
        assert(t.getProposingFantasyTeam().getId() == 1L);
        assert (t.getPlayerIn().getPlayerId() == 2L);
        assert (t.getPlayerOut().getPlayerId() == 1L);
    }
}
