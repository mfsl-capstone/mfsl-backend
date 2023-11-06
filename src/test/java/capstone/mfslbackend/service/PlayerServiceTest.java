package capstone.mfslbackend.service;

import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.Team;
import capstone.mfslbackend.repository.PlayerRepository;
import capstone.mfslbackend.response.container.PlayersContainer;
import capstone.mfslbackend.response.dto.PlayerResponse;
import capstone.mfslbackend.response.dto.PlayersResponse;
import capstone.mfslbackend.response.dto.TeamResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;


import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private TeamService teamService;
    @Mock
    private ApiService apiService;
    private final PlayerService playerService = new PlayerService(playerRepository, teamService, apiService, "http://test.url");
    @BeforeEach
    public void setup() throws IOException {
        Player player1 = new Player(1L, "player1", "Attacker", "p1.pic", 9, null);
        Player player2 = new Player(2L, "player2", "Defender", "p2.pic", 5, null);
        Team team1 = new Team(1L, "team1", "team1_url", List.of(player1, player2));
        player1.setTeam(team1);
        player2.setTeam(team1);

        lenient().when(teamService.getTeamById(1L))
                        .thenReturn(Optional.of(team1));
        lenient().when(playerRepository.save(Mockito.any()))
                .then(returnsFirstArg());
        lenient().when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player1));
        lenient().when(playerRepository.findById(2L))
                .thenReturn(Optional.of(player2));

        ReflectionTestUtils.setField(playerService, "apiService", apiService);
        ReflectionTestUtils.setField(playerService, "teamService", teamService);
        ReflectionTestUtils.setField(playerService, "playerRepository", playerRepository);
        ReflectionTestUtils.setField(playerService, "baseUrl", "http://test.url");

        TeamResponse teamResponse = new TeamResponse(1L, "team1", "team1_url", null, null, null, null, null, null);
        PlayerResponse playerResponse1 = new PlayerResponse(1L, "player1", "p1.pic", 9, "Attacker");
        PlayerResponse playerResponse2 = new PlayerResponse(2L, "player2", "p2.pic", 5, "Defender");
        PlayersResponse playersResponse = new PlayersResponse(teamResponse, List.of(playerResponse1, playerResponse2));
        PlayersContainer playersContainer = new PlayersContainer(null, null, 1, List.of(playersResponse));

        when(apiService.getRequest(any(), any()))
                .thenReturn(playersContainer);}

    @Test
    public void testCreateAllPlayersOnTeam_SuccessPlayersAlreadyExist()  {
        List<Player> players = playerService.createAllPlayersForTeam(1L);
        assertFalse(CollectionUtils.isEmpty(players));
        assertEquals(2, players.size());
        players = players.stream()
                .sorted(Comparator.comparingLong(Player::getPlayerId))
                .toList();

        assertEquals(1L, players.get(0).getPlayerId());
        assertEquals("Attacker", players.get(0).getPosition());
        assertEquals(9, players.get(0).getNumber());
        assertEquals(2L, players.get(1).getPlayerId());
        assertEquals("Defender", players.get(1).getPosition());
        assertEquals(5, players.get(1).getNumber());
    }

    @Test
    public void testCreateAllPlayersOnTeam_SuccessPlayersDontExist()  {
        when(playerRepository.findById(1L))
                .thenReturn(Optional.empty());
        when(playerRepository.findById(2L))
                .thenReturn(Optional.empty());

        List<Player> players = playerService.createAllPlayersForTeam(1L);
        assertFalse(CollectionUtils.isEmpty(players));
        assertEquals(2, players.size());
        players = players.stream()
                .sorted(Comparator.comparingLong(Player::getPlayerId))
                .toList();

        assertEquals(1L, players.get(0).getPlayerId());
        assertEquals("Attacker", players.get(0).getPosition());
        assertEquals(9, players.get(0).getNumber());
        assertEquals(2L, players.get(1).getPlayerId());
        assertEquals("Defender", players.get(1).getPosition());
        assertEquals(5, players.get(1).getNumber());
    }

    @Test
    public void testCreateAllPlayersOnTeam_ErrorSquadDoesntExist() throws IOException {
        when(apiService.getRequest(any(), any()))
                .thenReturn(null);
        List<Player> players = playerService.createAllPlayersForTeam(1L);
        assertNull(players);
    }
    @Test
    public void testCreateAllPlayersOnTeam_ErrorGettingSquad() throws IOException {
        when(apiService.getRequest(any(), any()))
                .thenThrow(new IOException("test exception"));
        List<Player> players = playerService.createAllPlayersForTeam(1L);
        assertNull(players);
    }

    @Test
    public void testCreateAllPlayersOnTeam_ErrorGettingTeam()  {
        lenient().when(teamService.getTeamById(anyLong()))
                .thenReturn(Optional.empty());
        List<Player> players = playerService.createAllPlayersForTeam(1L);
        assertTrue(CollectionUtils.isEmpty(players));
    }
}
