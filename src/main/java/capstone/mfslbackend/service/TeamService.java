package capstone.mfslbackend.service;

import capstone.mfslbackend.error.Error400;
import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.error.Error500;
import capstone.mfslbackend.model.Game;
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

    public List<Team> createTeamsInLeague(String leagueId, String season) {
        TeamsContainer teamsContainer;
        try {
            URL url = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/teams")
                    .queryParam("league", leagueId)
                    .queryParam("season", season)
                    .build().toUri().toURL();
            teamsContainer = apiService.getRequest(url, TeamsContainer.class);
        } catch (Exception e) {
            throw new Error500("Error creating teams for league: " + leagueId + " in season: " + season);
        }
        if (teamsContainer == null || CollectionUtils.isEmpty(teamsContainer.getResponse())) {
            throw new Error404("No teams found for league: " + leagueId + " in season: " + season);
        }

        return teamsContainer.getResponse().stream()
                .map(team -> {
                    try {
                        return getTeamById(team.getTeam().getId());
                    } catch (Error404 e) {
                        return createTeamById(team.getTeam().getId());
                    }
                })
                .toList();
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
            throw new Error500("Error finding team: " + teamId);
        }
        if (teamsContainer == null || CollectionUtils.isEmpty(teamsContainer.getResponse())) {
            throw new Error404("No team found with id: " + teamId);
        }
        try {
            return getTeamById(teamId);
        } catch (Error404 e) {
            return createTeam(teamsContainer.getResponse().get(0).getTeam());
        }
    }

    private Team createTeam(TeamResponse teamResponse) {
        Team team = new Team(teamResponse.getId(), teamResponse.getName(), teamResponse.getLogo(), new ArrayList<>(), new ArrayList<>());
        return teamRepository.save(team);
    }

    public Team getTeamById(long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new Error404("Could not find team with id " + teamId));
    }

    public List<Team> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        if (CollectionUtils.isEmpty(teams)) {
            log.warn("no teams exist");
        }
        return teams;
    }

    public void addGameToTeam(Long teamId, Game game) {
        Team team = getTeamById(teamId);

        if (!team.getGames().contains(game)) {
            List<Game> games = team.getGames();
            games.add(game);
            team.setGames(games);
            teamRepository.save(team);
        } else {
            throw new Error400("Game already exists for team: " + teamId);
        }
    }
}
