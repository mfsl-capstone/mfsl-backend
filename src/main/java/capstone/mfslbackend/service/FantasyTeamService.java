package capstone.mfslbackend.service;

import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.repository.FantasyTeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class FantasyTeamService {

    private final FantasyTeamRepository fantasyTeamRepository;
    public FantasyTeamService(FantasyTeamRepository fantasyTeamRepository) {
        this.fantasyTeamRepository = fantasyTeamRepository;
    }
    public Optional<FantasyTeam> getFantasyTeam(Long teamId) {
        Optional<FantasyTeam> fantasyTeam = fantasyTeamRepository.findById(teamId);
        if (fantasyTeam.isEmpty()) {
            log.warn("could not find team with id {}", teamId);
        }
        return fantasyTeam;
    }

    public FantasyTeam createFantasyTeam(String teamName) {
        FantasyTeam fantasyTeam = new FantasyTeam();
        fantasyTeam.setTeamName(teamName);
        fantasyTeamRepository.save(fantasyTeam);
        return fantasyTeam;
    }
}
