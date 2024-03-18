package capstone.mfslbackend.service;

import capstone.mfslbackend.model.FantasyLeague;
import capstone.mfslbackend.repository.FantasyLeagueRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@SpringBootTest
@Slf4j
public class FantasyLeagueServiceTest {
    @Mock
    private FantasyLeagueRepository fantasyLeagueRepository;
    private final FantasyLeagueService fantasyLeagueService = new FantasyLeagueService(null, null, null, null, null,  null);

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(fantasyLeagueService, "fantasyLeagueRepository", fantasyLeagueRepository);
        FantasyLeague fantasyLeague = new FantasyLeague(1L, "Test League", null);
        lenient().when(fantasyLeagueRepository.save(any())).thenReturn(fantasyLeague);
    }

    @Test
    public void testCreateFantasyLeague() {
        String name = "Test League";
        FantasyLeague fantasyLeague = fantasyLeagueService.createFantasyLeague(name);
        System.out.println(fantasyLeague);
        assert(fantasyLeague.getLeagueName().equals(name));
        assert(fantasyLeague.getId() == 1L);
    }
}
