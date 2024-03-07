package capstone.mfslbackend.service;

import capstone.mfslbackend.DTO.FantasyLeaguePlayer;
import capstone.mfslbackend.model.FantasyLeague;
import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.model.Player;
import capstone.mfslbackend.model.User;
import capstone.mfslbackend.repository.FantasyLeagueRepository;
import capstone.mfslbackend.repository.FantasyTeamRepository;
import capstone.mfslbackend.repository.PlayerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Slf4j
public class FantasyLeagueService {
    private final FantasyLeagueRepository fantasyLeagueRepository;
    private final UserService userService;
    private final PlayerService playerService;
    private final FantasyTeamRepository fantasyTeamRepository;
    private final EntityManager entityManager;

    public FantasyLeagueService(FantasyLeagueRepository fantasyLeagueRepository, UserService userService,
                                FantasyTeamRepository fantasyTeamRepository, PlayerService playerService,
                                EntityManager entityManager) {
        this.fantasyLeagueRepository = fantasyLeagueRepository;
        this.userService = userService;
        this.fantasyTeamRepository = fantasyTeamRepository;
        this.playerService = playerService;
        this.entityManager = entityManager;
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

    public List<FantasyLeaguePlayer> getFantasyLeaguePlayers(Long leagueId, List<Map<String, String>> filters, String sortDirection, String sortField, Boolean noTaken, int limit, int offset) {
        Optional<FantasyLeague> fantasyLeagueOptional = getFantasyLeagueById(leagueId);
        if (fantasyLeagueOptional.isEmpty()) {
            log.error("Fantasy League with id {} not found", leagueId);
            return new ArrayList<>();
        }

        List<Player> players = getTakenPlayersByFantasyLeagueId(leagueId);

        return playerService.getPlayers(players, filters, sortDirection, sortField, noTaken, limit, offset).stream()
                .map(player -> new FantasyLeaguePlayer(player, players.contains(player)))
                .toList();
    }
}
