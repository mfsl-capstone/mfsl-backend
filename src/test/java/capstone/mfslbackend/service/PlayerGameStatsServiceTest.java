package capstone.mfslbackend.service;

import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.Game;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;

@SpringBootTest
public class PlayerGameStatsServiceTest {
    @Mock
    private ApiService apiService;
    @Mock
    private PlayerGameStatsRepository playerGameStatsRepository;
    @Mock
    PlayerService playerService;
    @Mock
    GameService gameService;
    @Value("${YELLOW.CARD.POINTS}")
    private int yellowCardPoints;
    @Value("${RED.CARD.POINTS}")
    private int redCardPoints;
    @Value("${MINUTES.POINTS}")
    private int minutesPoints;
    @Value("${MINUTES.THRESHOLD}")
    private int minutesThreshold;
    @Value("${PENALTY.COMMITTED.POINTS}")
    private int penaltyCommittedPoints;
    @Value("${FOULS.COMMITTED.THRESHOLD}")
    private int foulsCommittedThreshold;
    @Value("${PENALTY.MISSED.POINTS}")
    private int penaltyMissedPoints;
    @Value("${SAVES.POINTS}")
    private int savesPoints;
    @Value("${SAVES.THRESHOLD}")
    private int savesThreshold;
    @Value("${GOALS.CONCEDED.THRESHOLD}")
    private int goalsConcededThreshold;
    @Value("${PENALTIES.SAVED.POINTS}")
    private int penaltiesSavedPoints;
    @Value("${GK.CLEAN.SHEET.POINTS}")
    private int gkCleanSheetPoints;
    @Value("${DEF.CLEAN.SHEET.POINTS}")
    private int defCleanSheetPoints;
    @Value("${MID.CLEAN.SHEET.POINTS}")
    private int midCleanSheetPoints;
    @Value("${GK.GOALS.SCORED.POINTS}")
    private int gkGoalsScoredPoints;
    @Value("${GK.ASSISTS.POINTS}")
    private int gkAssistsPoints;
    @Value("${DEF.GOALS.SCORED.POINTS}")
    private int defGoalsScoredPoints;
    @Value("${MID.GOALS.SCORED.POINTS}")
    private int midGoalsScoredPoints;
    @Value("${ATT.GOALS.SCORED.POINTS}")
    private int attGoalsScoredPoints;
    @Value("${DEF.ASSISTS.POINTS}")
    private int defAssistsPoints;
    @Value("${MID.ASSISTS.POINTS}")
    private int midAssistsPoints;
    @Value("${ATT.ASSISTS.POINTS}")
    private int attAssistsPoints;
    @Value("${SHOT.ACCURACY.THRESHOLD}")
    private int shotAccuracyThreshold;
    @Value("${RATING.POINTS}")
    private int ratingPoints;
    @Value("${FRACTION.TO.PERCENT}")
    private int fractionToPercent;
    @Value("${RATING.THRESHOLD.1}")
    private int ratingThreshold1;
    @Value("${RATING.THRESHOLD.2}")
    private double ratingThreshold2;
    @Value("${RATING.THRESHOLD.3}")
    private int ratingThreshold3;
    @Value("${RATING.THRESHOLD.4}")
    private int ratingThreshold4;

    private final PlayerGameStatsService playerGameStatsService = new PlayerGameStatsService(apiService, playerGameStatsRepository, playerService,"http://test.url", gameService);    @BeforeEach
    public void setup() throws IOException {
        ReflectionTestUtils.setField(playerGameStatsService, "apiService", apiService);
        ReflectionTestUtils.setField(playerGameStatsService, "baseUrl", "http://test.url");
        ReflectionTestUtils.setField(playerGameStatsService, "playerGameStatsRepository", playerGameStatsRepository);
        ReflectionTestUtils.setField(playerGameStatsService, "playerService", playerService);
        ReflectionTestUtils.setField(playerGameStatsService, "gameService", gameService);
        ReflectionTestUtils.setField(playerGameStatsService, "yellowCardPoints", yellowCardPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "redCardPoints", redCardPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "minutesPoints", minutesPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "minutesThreshold", minutesThreshold);
        ReflectionTestUtils.setField(playerGameStatsService, "penaltyCommittedPoints", penaltyCommittedPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "penaltyMissedPoints", penaltyMissedPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "savesPoints", savesPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "savesThreshold", savesThreshold);
        ReflectionTestUtils.setField(playerGameStatsService, "goalsConcededThreshold", goalsConcededThreshold);
        ReflectionTestUtils.setField(playerGameStatsService, "penaltiesSavedPoints", penaltiesSavedPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "gkCleanSheetPoints", gkCleanSheetPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "defCleanSheetPoints", defCleanSheetPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "midCleanSheetPoints", midCleanSheetPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "gkGoalsScoredPoints", gkGoalsScoredPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "gkAssistsPoints", gkAssistsPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "defGoalsScoredPoints", defGoalsScoredPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "midGoalsScoredPoints", midGoalsScoredPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "attGoalsScoredPoints", attGoalsScoredPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "defAssistsPoints", defAssistsPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "midAssistsPoints", midAssistsPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "attAssistsPoints", attAssistsPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "shotAccuracyThreshold", shotAccuracyThreshold);
        ReflectionTestUtils.setField(playerGameStatsService, "ratingPoints", ratingPoints);
        ReflectionTestUtils.setField(playerGameStatsService, "fractionToPercent", fractionToPercent);
        ReflectionTestUtils.setField(playerGameStatsService, "ratingThreshold1", ratingThreshold1);
        ReflectionTestUtils.setField(playerGameStatsService, "ratingThreshold2", ratingThreshold2);
        ReflectionTestUtils.setField(playerGameStatsService, "ratingThreshold3", ratingThreshold3);
        ReflectionTestUtils.setField(playerGameStatsService, "ratingThreshold4", ratingThreshold4);

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
        playersStatsResponse3.setGoals(new GoalsResponse(1,2));
        statsContainer2.setResponse(List.of(playersStatsResponse3));
        lenient().when(apiService.getRequest(eq(UriComponentsBuilder.fromUriString("http://test.url")
                .path("/fixtures")
                .queryParam("id", "1")
                .build().toUri().toURL()), eq(StatsContainer.class)))
                .thenReturn(statsContainer2);
        Player p = new Player();
        lenient().when(playerService.getPlayerById(any()))
                .thenReturn(p);

        Game g = new Game();
        lenient().when(gameService.getGameById(anyLong()))
                .thenReturn(g);
    }

    @Test
    public void testCreatePlayerGameStats() throws Error404 {
        List<PlayerGameStats> response = playerGameStatsService.createPlayerGameStats("1");
        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    public void testErrorCreatePlayerGameStats() throws IOException, Error404 {
        lenient().when(apiService.getRequest(eq(UriComponentsBuilder.fromUriString("http://test.url")
                .path("/fixtures")
                .queryParam("id", "1")
                .build().toUri().toURL()), eq(StatsContainer.class)))
                .thenThrow(new RuntimeException());
        try {
            playerGameStatsService.createPlayerGameStats("1");
        } catch (Error404 e) {
            assertEquals("Could not find fixture: 1", e.getMessage());
            return;
        }
        fail();
    }
}
