package capstone.mfslbackend.service;

import capstone.mfslbackend.model.FantasyLeague;
import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.User;
import capstone.mfslbackend.repository.FantasyLeagueRepository;
import capstone.mfslbackend.repository.FantasyTeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FantasyLeagueService {
    private final FantasyLeagueRepository fantasyLeagueRepository;
    private final UserService userService;
    private final FantasyTeamRepository fantasyTeamRepository;

    public FantasyLeagueService(FantasyLeagueRepository fantasyLeagueRepository, UserService userService,
                                FantasyTeamRepository fantasyTeamRepository) {
        this.fantasyLeagueRepository = fantasyLeagueRepository;
        this.userService = userService;
        this.fantasyTeamRepository = fantasyTeamRepository;
    }
    public FantasyLeague createFantasyLeague(String leagueName) {
        FantasyLeague fantasyLeague = new FantasyLeague();
        fantasyLeague.setLeagueName(leagueName);
        return fantasyLeagueRepository.save(fantasyLeague);
    }
    public Optional<FantasyLeague> getFantasyLeagueById(Long fantasyLeagueId) {
        return fantasyLeagueRepository.findById(fantasyLeagueId);
    }
    public List<FantasyLeague> getFantasyLeagueByName(String fantasyLeagueName) {
        String name = "%" + fantasyLeagueName + "%";
        return fantasyLeagueRepository.findFantasyLeagueByLeagueNameLikeIgnoreCase(name);
    }

    public FantasyLeague joinFantasyLeague(String username, Long leagueId, String teamName) {
        Optional<User> userOptional = userService.getUser(username);
        Optional<FantasyLeague> leagueOptional = fantasyLeagueRepository.findById(leagueId);
        FantasyTeam fantasyTeam = new FantasyTeam();
        fantasyTeam.setTeamName(teamName);

        if (userOptional.isEmpty() || leagueOptional.isEmpty()) {
            return null;
        }
        FantasyLeague league = leagueOptional.get();
        User user = userOptional.get();

//        block users from registering two teams in a league
        for (FantasyTeam team : league.getFantasyTeams()) {
            if (team.getUser().equals(user)) {
                return null;
            }
        }


        fantasyTeam.setUser(user);
        fantasyTeam.setFantasyLeague(league);
        fantasyTeamRepository.save(fantasyTeam);

//        we can assume league is present since we already got it with the same id
        return fantasyLeagueRepository.findById(leagueId).get();
    }

    public List<Player> getTakenPlayersByFantasyLeagueId(Long fantasyLeagueId) {
        Optional<FantasyLeague> fantasyLeagueOptional = getFantasyLeagueById(fantasyLeagueId);
        if (fantasyLeagueOptional.isEmpty()) {
            log.error("Fantasy League with id {} not found", fantasyLeagueId);
            throw new IllegalArgumentException("Fantasy League with id " + fantasyLeagueId + " not found");
        }

        List<Player> players = new ArrayList<>();
        FantasyLeague fantasyLeague = fantasyLeagueOptional.get();
        fantasyLeague.getFantasyTeams().forEach(fantasyTeam -> players.addAll(fantasyTeam.getPlayers()));
        return players;
    }

    public Optional<FantasyTeam> getFantasyTeamOfTakenPlayer(Long fantasyLeagueId, Long playerId) {
        Optional<FantasyLeague> fantasyLeagueOptional = getFantasyLeagueById(fantasyLeagueId);
        if (fantasyLeagueOptional.isEmpty()) {
            log.error("Fantasy League with id {} not found", fantasyLeagueId);
            throw new IllegalArgumentException("Fantasy League with id " + fantasyLeagueId + " not found");
        }

        return fantasyLeagueOptional
                .map(FantasyLeague::getFantasyTeams)
                .orElse(Collections.emptySet())
                .stream()
                .filter(fantasyTeam -> fantasyTeam.getPlayers().stream()
                        .anyMatch(player -> player.getPlayerId().equals(playerId)))
                .findFirst();
    }
}
