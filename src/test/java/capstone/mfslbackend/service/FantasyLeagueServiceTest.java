package capstone.mfslbackend.service;

import capstone.mfslbackend.model.FantasyLeague;
import capstone.mfslbackend.repository.DraftRepository;
import capstone.mfslbackend.repository.FantasyLeagueRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@SpringBootTest
@Slf4j
public class FantasyLeagueServiceTest {
    @Mock
    private FantasyLeagueRepository fantasyLeagueRepository;
    @Mock
    private DraftRepository draftRepository;
    private final FantasyLeagueService fantasyLeagueService = new FantasyLeagueService(null, null, null, null, null, null,  null);

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(fantasyLeagueService, "fantasyLeagueRepository", fantasyLeagueRepository);
        ReflectionTestUtils.setField(fantasyLeagueService, "draftRepository", draftRepository);
        FantasyLeague fantasyLeague = new FantasyLeague(1L, "Test League", null, null);
        lenient().when(fantasyLeagueRepository.save(any())).thenReturn(fantasyLeague);
        lenient().when(draftRepository.save(any())).thenReturn(null);
    }

    @Test
    public void testCreateFantasyLeague() {
        String name = "Test League";
        FantasyLeague fantasyLeague = fantasyLeagueService.createFantasyLeague(name, LocalDateTime.now());
        System.out.println(fantasyLeague);
        assert(fantasyLeague.getLeagueName().equals(name));
        assert(fantasyLeague.getId() == 1L);
    }
}
