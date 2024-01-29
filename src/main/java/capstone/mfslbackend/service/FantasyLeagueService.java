package capstone.mfslbackend.service;

import capstone.mfslbackend.model.FantasyLeague;
import capstone.mfslbackend.repository.FantasyLeagueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FantasyLeagueService {
    private final FantasyLeagueRepository fantasyLeagueRepository;
    public FantasyLeagueService(FantasyLeagueRepository fantasyLeagueRepository) {
        this.fantasyLeagueRepository = fantasyLeagueRepository;
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
}
