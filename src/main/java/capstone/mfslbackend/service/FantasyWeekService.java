package capstone.mfslbackend.service;

import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.model.FantasyWeek;
import capstone.mfslbackend.repository.FantasyWeekRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class FantasyWeekService {

    private final FantasyWeekRepository fantasyWeekRepository;
    private final FantasyTeamService fantasyTeamService;
    public FantasyWeekService(FantasyWeekRepository fantasyWeekRepository, FantasyTeamService fantasyTeamService) {
        this.fantasyWeekRepository = fantasyWeekRepository;
        this.fantasyTeamService = fantasyTeamService;
    }
    public Optional<FantasyWeek> getFantasyWeekById(Long weekId) {
        Optional<FantasyWeek> fantasyWeek = fantasyWeekRepository.findById(weekId);
        if (fantasyWeek.isEmpty()) {
            log.warn("could not find week with id {}", weekId);
        }
        return fantasyWeek;
    }

    public List<FantasyWeek> getFantasyWeekByWeekNumber(int weekNumber) {
        return fantasyWeekRepository.findByWeekNumber(weekNumber);
    }
    public FantasyWeek createFantasyWeek(int fantasyTeamId, int weekNumber) {
        if (weekNumber <= 0) {
            log.warn("week number cannot be negative");
            return null;
        }
        FantasyTeam fantasyTeam = fantasyTeamService.getFantasyTeam((long) fantasyTeamId).orElse(null);
        if (fantasyTeam == null) {
            log.warn("could not find fantasy team with id {}", fantasyTeamId);
            return null;
        }
        FantasyWeek fantasyWeek = new FantasyWeek();
        fantasyWeek.setWeekNumber(weekNumber);
        fantasyWeek.setFantasyTeam(fantasyTeam);
        fantasyWeekRepository.save(fantasyWeek);
        return fantasyWeek;
    }
}
