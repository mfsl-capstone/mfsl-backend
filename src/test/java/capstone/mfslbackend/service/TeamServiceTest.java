package capstone.mfslbackend.service;

import capstone.mfslbackend.model.Team;
import capstone.mfslbackend.repository.TeamRepository;
import capstone.mfslbackend.response.container.TeamsContainer;
import capstone.mfslbackend.response.dto.TeamResponse;
import capstone.mfslbackend.response.dto.TeamsResponse;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.lenient;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ApiService apiService;

    private final TeamService teamService = new TeamService(teamRepository, apiService,"https://test.com");

    @Before
    public void setup() {
        lenient().when(teamRepository.save(Mockito.any()))
                .then(returnsFirstArg());
    }

    @Test
    public void testCreateTeamsInLeague_SuccessTeamDoesntExist() throws IOException {
        // Arrange
        String leagueId = "123";
        String season = "2023";

        Long teamId = 1L;
        String teamName = "test";
        // Mock the ApiService to return a list of teams
        TeamsContainer teamsContainer = new TeamsContainer();
        List<TeamsResponse> teamsResponses = new ArrayList<>();

        TeamResponse teamResponse1 = new TeamResponse();
        teamResponse1.setId(teamId);
        teamResponse1.setName(teamName);
        teamResponse1.setLogo(teamName);

        teamsResponses.add(new TeamsResponse(teamResponse1));
        teamsContainer.setResponse(teamsResponses);

        Mockito.when(apiService.getRequest(Mockito.any(), Mockito.eq(TeamsContainer.class)))
                .thenReturn(teamsContainer);

        // Act
        ResponseEntity<List<Team>> responseEntity = teamService.createTeamsInLeague(leagueId, season);

        // Assert
        assertEquals(200, responseEntity.getStatusCode().value()); // Status code should be OK
        List<Team> teams = responseEntity.getBody();
        assertFalse(CollectionUtils.isEmpty(teams)); // Teams should not be empty
        assertEquals(teamId, teams.get(0).getTeamId());
        assertEquals(teamName, teams.get(0).getName());
    }

    @Test
    public void testCreateTeamsInLeague_SuccessExists() throws IOException {
        // Arrange
        String leagueId = "123";
        String season = "2023";

        long teamId = 1L;
        String teamName = "test";
        // Mock the ApiService to return a list of teams
        TeamsContainer teamsContainer = new TeamsContainer();
        List<TeamsResponse> teamsResponses = new ArrayList<>();

        TeamResponse teamResponse1 = new TeamResponse();
        teamResponse1.setId(teamId);
        teamResponse1.setName(teamName);
        teamResponse1.setLogo(teamName);

        teamsResponses.add(new TeamsResponse(teamResponse1));
        teamsContainer.setResponse(teamsResponses);

        Mockito.when(apiService.getRequest(Mockito.any(), Mockito.eq(TeamsContainer.class)))
                .thenReturn(teamsContainer);
        Long newTeamId = 2L;
        Team team = new Team(newTeamId, teamName, teamName, new ArrayList<>());
        Mockito.when(teamRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(team));

        // Act
        ResponseEntity<List<Team>> responseEntity = teamService.createTeamsInLeague(leagueId, season);

        // Assert
        assertEquals(200, responseEntity.getStatusCode().value()); // Status code should be OK
        List<Team> teams = responseEntity.getBody();
        assertFalse(CollectionUtils.isEmpty(teams)); // Teams should not be empty
        assertEquals(newTeamId, teams.get(0).getTeamId());
        assertEquals(teamName, teams.get(0).getName());
    }

    @Test
    public void testCreateTeamsInLeague_Error() throws IOException {
        Mockito.when(apiService.getRequest(Mockito.any(), Mockito.eq(TeamsContainer.class)))
                .thenThrow(new IOException("test exception"));
        ResponseEntity<List<Team>> responseEntity = teamService.createTeamsInLeague("1", "2024");
        assertEquals(404, responseEntity.getStatusCode().value());
    }

    @Test
    public void testCreateTeamsInLeague_NotFound() throws IOException {
        Mockito.when(apiService.getRequest(Mockito.any(), Mockito.eq(TeamsContainer.class)))
                .thenReturn(new TeamsContainer());
        ResponseEntity<List<Team>> responseEntity = teamService.createTeamsInLeague("1", "2024");
        assertEquals(404, responseEntity.getStatusCode().value());
    }

    @Test
    public void testCreateTeamById_SuccessDidNotExist() throws IOException {
        Long teamId = 1L;
        String teamName = "test";
        TeamsContainer teamsContainer = new TeamsContainer();
        List<TeamsResponse> teamsResponses = new ArrayList<>();

        TeamResponse teamResponse1 = new TeamResponse();
        teamResponse1.setId(teamId);
        teamResponse1.setName(teamName);
        teamResponse1.setLogo(teamName);

        teamsResponses.add(new TeamsResponse(teamResponse1));
        teamsContainer.setResponse(teamsResponses);

        Mockito.when(apiService.getRequest(Mockito.any(), Mockito.eq(TeamsContainer.class)))
                .thenReturn(teamsContainer);

        Team team = teamService.createTeamById(teamId);
        assertNotNull(team);
        assertEquals(teamId, team.getTeamId());
        assertEquals(teamName, team.getName());
        assertEquals(teamName, team.getUrl());
    }

    @Test
    public void testCreateTeamById_SuccessExistsAlready() throws IOException {
        long teamId = 1L;
        String teamName = "test";
        TeamsContainer teamsContainer = new TeamsContainer();
        List<TeamsResponse> teamsResponses = new ArrayList<>();

        TeamResponse teamResponse1 = new TeamResponse();
        teamResponse1.setId(teamId);
        teamResponse1.setName(teamName);
        teamResponse1.setLogo(teamName);

        teamsResponses.add(new TeamsResponse(teamResponse1));
        teamsContainer.setResponse(teamsResponses);

        Mockito.when(apiService.getRequest(Mockito.any(), Mockito.eq(TeamsContainer.class)))
                .thenReturn(teamsContainer);
        Long newTeamId = 2L;
        Team team2 = new Team(newTeamId, teamName, teamName, new ArrayList<>());
        Mockito.when(teamRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(team2));

        Team team = teamService.createTeamById(teamId);
        assertNotNull(team);
        assertEquals(newTeamId, team.getTeamId());
        assertEquals(teamName, team.getName());
        assertEquals(teamName, team.getUrl());
    }

    @Test
    public void testCreateTeamById_Error() throws IOException {
        Mockito.when(apiService.getRequest(Mockito.any(), Mockito.eq(TeamsContainer.class)))
                .thenThrow(new IOException("test exception"));
        Team team = teamService.createTeamById(1L);
        assertNull(team);
    }

    @Test
    public void testCreateTeamById_NotFound() throws IOException {
        Mockito.when(apiService.getRequest(Mockito.any(), Mockito.eq(TeamsContainer.class)))
                .thenReturn(new TeamsContainer());
        Team team = teamService.createTeamById(1L);
        assertNull(team);
    }
}
