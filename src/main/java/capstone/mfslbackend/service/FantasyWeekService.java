package capstone.mfslbackend.service;

import capstone.mfslbackend.model.FantasyWeek;
import capstone.mfslbackend.repository.FantasyWeekRepository;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class FantasyWeekService {

    private final FantasyWeekRepository fantasyWeekRepository;
    public FantasyWeekService(FantasyWeekRepository fantasyWeekRepository) {
        this.fantasyWeekRepository = fantasyWeekRepository;
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
    public FantasyWeek createFantasyWeek(int weekNumber) {
        if (weekNumber < 1){
            log.warn("week number cannot be negative");
        }
        FantasyWeek fantasyWeek = new FantasyWeek();
        fantasyWeek.setWeekNumber(weekNumber);
        fantasyWeekRepository.save(fantasyWeek);
        return fantasyWeek;
    }
}
