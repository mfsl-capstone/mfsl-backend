package capstone.mfslbackend.controller;


import capstone.mfslbackend.DTO.FantasyWeekForMatchupDTO;
import capstone.mfslbackend.DTO.FantasyWeekForTableDTO;
import capstone.mfslbackend.factory.FantasyWeekForMatchupDTOFactory;
import capstone.mfslbackend.model.FantasyWeek;
import capstone.mfslbackend.service.FantasyWeekService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.List;
@RestController()
@RequestMapping("fantasy-week")
public class FantasyWeekController {
    private final FantasyWeekService fantasyWeekService;
    private final FantasyWeekForMatchupDTOFactory fantasyWeekForMatchupDTOFactory;
    public FantasyWeekController(FantasyWeekService fantasyWeekService,
                                 FantasyWeekForMatchupDTOFactory fantasyWeekForMatchupDTOFactory) {
        this.fantasyWeekService = fantasyWeekService;
        this.fantasyWeekForMatchupDTOFactory = fantasyWeekForMatchupDTOFactory;
    }

    @GetMapping("{fantasyWeekId}")
    public ResponseEntity<FantasyWeekForMatchupDTO> getFantasyWeekById(@PathVariable long fantasyWeekId) {
        FantasyWeek fantasyWeek = fantasyWeekService.getFantasyWeekById(fantasyWeekId);
        return ResponseEntity.ok(fantasyWeekForMatchupDTOFactory.from(fantasyWeek));
    }

    @GetMapping("")
    public ResponseEntity<FantasyWeekForMatchupDTO> getFantasyWeekByWeekNumber(@RequestParam long fantasyLeagueId, @RequestParam long fantasyTeamId,
                                                                  @RequestParam int weekNumber) {
        FantasyWeek fantasyWeek = fantasyWeekService.getFantasyWeekByWeekNumber(fantasyLeagueId, fantasyTeamId, weekNumber);
        return ResponseEntity.ok(fantasyWeekForMatchupDTOFactory.from(fantasyWeek));
    }
    @GetMapping("{fantasyLeagueId}")
    public ResponseEntity<List<FantasyWeekForTableDTO>> getFantasyWeeksByLeagueId(@PathVariable long fantasyLeagueId) {
        List<FantasyWeek> fantasyWeeks = fantasyWeekService.getFantasyWeeksByLeagueId(fantasyLeagueId);
        return ResponseEntity.ok(fantasyWeeks.stream().map(week -> new FantasyWeekForTableDTO().from(week)).toList());
    }

    @GetMapping("active")
    public ResponseEntity<FantasyWeekForMatchupDTO> getActiveFantasyWeek(@RequestParam long fantasyLeagueId, @RequestParam long fantasyTeamId) {
        FantasyWeek fantasyWeek = fantasyWeekService.getActiveFantasyWeek(fantasyLeagueId, fantasyTeamId);
        return ResponseEntity.ok(fantasyWeekForMatchupDTOFactory.from(fantasyWeek));

    }

    @GetMapping("date")
    public ResponseEntity<FantasyWeekForMatchupDTO> getFantasyWeekByDate(@RequestParam long fantasyLeagueId, @RequestParam long fantasyTeamId,
                                                            @RequestParam LocalDate date) {
        FantasyWeek fantasyWeek = fantasyWeekService.getFantasyWeekByDate(fantasyLeagueId, fantasyTeamId, date);
        return ResponseEntity.ok(fantasyWeekForMatchupDTOFactory.from(fantasyWeek));
    }
    @PostMapping("start")
    public ResponseEntity<Boolean> startActiveFantasyWeeks() {
        fantasyWeekService.startActiveFantasyWeeks();
        return ResponseEntity.ok(true);
    }
    @PostMapping("end")
    public ResponseEntity<Boolean> endActiveFantasyWeeks() {
        fantasyWeekService.updateActiveFantasyWeeks();
        return ResponseEntity.ok(true);
    }
}
