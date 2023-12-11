package capstone.mfslbackend.service;

import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.PlayerGameStats;
import capstone.mfslbackend.repository.PlayerGameStatsRepository;
import capstone.mfslbackend.response.container.StatsContainer;
import capstone.mfslbackend.response.dto.*;
import capstone.mfslbackend.response.dto.stats.PassResponse;
import capstone.mfslbackend.response.dto.stats.StatisticResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

@SpringBootTest
public class PlayerGameStatsServiceTest {
    @Mock
    private ApiService apiService;
    @Mock
    private PlayerGameStatsRepository playerGameStatsRepository;
    @Mock
    PlayerService playerService;
    private final PlayerGameStatsService playerGameStatsService = new PlayerGameStatsService(apiService, playerGameStatsRepository, playerService,"http://test.url");
    @BeforeEach
    public void setup() throws IOException {
        ReflectionTestUtils.setField(playerGameStatsService, "apiService", apiService);
        ReflectionTestUtils.setField(playerGameStatsService, "baseUrl", "http://test.url");
        ReflectionTestUtils.setField(playerGameStatsService, "playerGameStatsRepository", playerGameStatsRepository);
        ReflectionTestUtils.setField(playerGameStatsService, "playerService", playerService);

        StatsContainer statsContainer = new StatsContainer();
        PlayersStatsResponse playersStatsResponse = new PlayersStatsResponse();

        TeamResponse teamResponse = new TeamResponse();
        teamResponse.setId(1L);
        playersStatsResponse.setTeam(teamResponse);

        PlayerStatsResponse playerStatsResponse = new PlayerStatsResponse();
        PlayerResponse playerResponse = new PlayerResponse();
        playerResponse.setId(1L);
        playerStatsResponse.setPlayer(playerResponse);
        StatisticResponse statisticResponse = new StatisticResponse();
        PassResponse passResponse = new PassResponse();
        passResponse.setTotal(13);
        statisticResponse.setPasses(passResponse);
        playerStatsResponse.setStatistics(List.of(statisticResponse));
        playersStatsResponse.setPlayers(List.of(playerStatsResponse));

        PlayersStatsResponse playersStatsResponse2 = new PlayersStatsResponse();
        TeamResponse teamResponse2 = new TeamResponse();
        teamResponse2.setId(2L);
        playersStatsResponse.setTeam(teamResponse2);

        PlayerStatsResponse playerStatsResponse2 = new PlayerStatsResponse();
        PlayerResponse playerResponse2 = new PlayerResponse();
        playerResponse2.setId(2L);
        playerStatsResponse2.setPlayer(playerResponse2);
        StatisticResponse statisticResponse2 = new StatisticResponse();
        PassResponse passResponse2 = new PassResponse();
        passResponse2.setTotal(13);
        statisticResponse2.setPasses(passResponse2);
        playerStatsResponse2.setStatistics(List.of(statisticResponse2));
        playersStatsResponse2.setPlayers(List.of(playerStatsResponse2));
        statsContainer.setResponse(List.of(playersStatsResponse, playersStatsResponse2));

        lenient().when(apiService.getRequest(eq(UriComponentsBuilder.fromUriString("http://test.url")
                .path("/fixtures/players")
                .queryParam("fixture", 1L)
                .build().toUri().toURL()), eq(StatsContainer.class)))
                .thenReturn(statsContainer);

        StatsContainer statsContainer2 = new StatsContainer();
        PlayersStatsResponse playersStatsResponse3 = new PlayersStatsResponse();
        TeamsResponse teamsResponse = new TeamsResponse();
        TeamResponse teamResponse3 = new TeamResponse();
        teamResponse3.setId(1L);
        teamResponse3.setWinner(true);
        teamsResponse.setHome(teamResponse3);
        TeamResponse teamResponse4 = new TeamResponse();
        teamResponse4.setId(1L);
        teamResponse4.setWinner(true);
        teamsResponse.setAway(teamResponse4);
        LeagueResponse leagueResponse = new LeagueResponse();
        leagueResponse.setRound("Regular Season - 1");
        playersStatsResponse3.setLeague(leagueResponse);
        playersStatsResponse3.setTeams(teamsResponse);
        statsContainer2.setResponse(List.of(playersStatsResponse3));
        lenient().when(apiService.getRequest(eq(UriComponentsBuilder.fromUriString("http://test.url")
                .path("/fixtures")
                .queryParam("id", "1")
                .build().toUri().toURL()), eq(StatsContainer.class)))
                .thenReturn(statsContainer2);
        Player p = new Player();
        lenient().when(playerService.getPlayerById(any()))
                .thenReturn(Optional.of(p));
    }

    @Test
    public void testCreatePlayerGameStats() {
        ResponseEntity<List<PlayerGameStats>> response = playerGameStatsService.createPlayerGameStats("1");
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void testErrorCreatePlayerGameStats() throws IOException {
        lenient().when(apiService.getRequest(eq(UriComponentsBuilder.fromUriString("http://test.url")
                .path("/fixtures")
                .queryParam("id", "1")
                .build().toUri().toURL()), eq(StatsContainer.class)))
                .thenThrow(new RuntimeException());
        ResponseEntity<List<PlayerGameStats>> response = playerGameStatsService.createPlayerGameStats("1");
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}
