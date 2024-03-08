package capstone.mfslbackend.service;

import capstone.mfslbackend.error.Error400;
import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.FantasyTeam;
import capstone.mfslbackend.model.FantasyWeek;
import capstone.mfslbackend.repository.FantasyWeekRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FantasyWeekService {

    private final FantasyWeekRepository fantasyWeekRepository;
    private final FantasyTeamService fantasyTeamService;
    public FantasyWeekService(FantasyWeekRepository fantasyWeekRepository, FantasyTeamService fantasyTeamService) {
        this.fantasyWeekRepository = fantasyWeekRepository;
        this.fantasyTeamService = fantasyTeamService;
    }
    public FantasyWeek getFantasyWeekById(Long weekId) throws Error404 {
        return fantasyWeekRepository.findById(weekId)
                .orElseThrow(() -> new Error404("could not find week with id " + weekId));
    }

    public List<FantasyWeek> getFantasyWeekByWeekNumber(int weekNumber) {
        return fantasyWeekRepository.findByWeekNumber(weekNumber);
    }
    public FantasyWeek createFantasyWeek(int fantasyTeamId, int weekNumber) throws Error404, Error400 {
        if (weekNumber <= 0) {
            throw new Error400("week number cannot be negative");
        }
        FantasyTeam fantasyTeam = fantasyTeamService.getFantasyTeam((long) fantasyTeamId);
        FantasyWeek fantasyWeek = new FantasyWeek();
        fantasyWeek.setWeekNumber(weekNumber);
        fantasyWeek.setFantasyTeam(fantasyTeam);
        fantasyWeekRepository.save(fantasyWeek);
        return fantasyWeek;
    }
}
