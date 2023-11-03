package capstone.mfslbackend.service;

import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.Team;
import capstone.mfslbackend.repository.TeamRepository;
import capstone.mfslbackend.response.container.TeamsContainer;
import capstone.mfslbackend.response.dto.TeamResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TeamService {

    private final String baseUrl;
    private final TeamRepository teamRepository;
    private final ApiService apiService;

    public TeamService(TeamRepository teamRepository, ApiService apiService,
                       @Value("${base.url}") String baseUrl) {
        this.teamRepository = teamRepository;
        this.apiService = apiService;
        this.baseUrl = baseUrl;
    }

    public ResponseEntity<List<Team>> createTeamsInLeague(String leagueId, String season) {
        TeamsContainer teamsContainer;
        try {
            URL url = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/teams")
                    .queryParam("league", leagueId)
                    .queryParam("season", season)
                    .build().toUri().toURL();
            teamsContainer = apiService.getRequest(url, TeamsContainer.class);
        } catch (Exception e) {
            log.error("Error finding teams in league: {} in season: {}", leagueId, season, e);
            return ResponseEntity.notFound().build();
        }
        if (teamsContainer == null || CollectionUtils.isEmpty(teamsContainer.getResponse())) {
            log.error("No teams found in league: {} in season: {}", leagueId, season);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(teamsContainer.getResponse().stream()
                .map(team -> getTeamById(team.getTeam().getId())
                        .orElse(createTeamById(team.getTeam().getId())))
                .toList());
    }

    public Team createTeamById(Long teamId) {
        TeamsContainer teamsContainer;
        try {
            URL url = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/teams")
                    .queryParam("id", teamId)
                    .build().toUri().toURL();
            teamsContainer = apiService.getRequest(url, TeamsContainer.class);
        } catch (Exception e) {
            log.error("Error finding team: {} for creating", teamId, e);
            return null;
        }
        if (teamsContainer == null || CollectionUtils.isEmpty(teamsContainer.getResponse())) {
            log.error("Team {} not found for creation", teamId);
            return null;
        }
        return getTeamById(teamId).orElse(createTeam(teamsContainer.getResponse().get(0).getTeam()));
    }

    private Team createTeam(TeamResponse teamResponse) {
        Team team = new Team(teamResponse.getId(), teamResponse.getName(), teamResponse.getLogo(), new ArrayList<>());
        return teamRepository.save(team);
    }

    public Optional<Team> getTeamById(long teamId) {
        Optional<Team> team = teamRepository.findById(teamId);
        if (team.isEmpty()) log.warn("could not find team with id {}", teamId);
        return team;
    }

    public List<Team> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        if (CollectionUtils.isEmpty(teams)) log.warn("no teams exist");
        return teams;
    }

    public List<Player> getPlayersOnTeam(Long teamId) {
        Optional<Team> team = getTeamById(teamId);
        if (team.isEmpty()) return new ArrayList<>();
        return team.get().getPlayers();
    }
}
