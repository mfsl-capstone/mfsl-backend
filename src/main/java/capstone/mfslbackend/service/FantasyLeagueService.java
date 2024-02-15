package capstone.mfslbackend.service;

import capstone.mfslbackend.model.FantasyLeague;
import capstone.mfslbackend.model.User;
import capstone.mfslbackend.repository.FantasyLeagueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FantasyLeagueService {
    private final FantasyLeagueRepository fantasyLeagueRepository;
    private final UserService userService;

    public FantasyLeagueService(FantasyLeagueRepository fantasyLeagueRepository, UserService userService) {
        this.fantasyLeagueRepository = fantasyLeagueRepository;
        this.userService = userService;
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

    public FantasyLeague joinFantasyLeague(String username, Long leagueId) {
            Optional<FantasyLeague> fantasyLeagueOptional = getFantasyLeagueById(leagueId);
            if (fantasyLeagueOptional.isEmpty()) {
                log.error("Fantasy League with id {} not found", leagueId);
                throw new IllegalArgumentException("Fantasy League with id " + leagueId + " not found");
            }
            if (userService.getUser(username) == null) {
                log.error("User with username {} not found", username);
                throw new IllegalArgumentException("User with username " + username + " not found");
            }
            FantasyLeague fantasyLeague = fantasyLeagueOptional.get();
            User user = userService.getUser(username);
            fantasyLeague.getUsers().add(user);
            return fantasyLeagueRepository.save(fantasyLeague);
        }

    }
