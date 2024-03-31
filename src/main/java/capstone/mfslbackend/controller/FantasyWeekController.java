package capstone.mfslbackend.controller;


import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.FantasyWeek;
import capstone.mfslbackend.service.FantasyWeekService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@RestController()
@RequestMapping("/fantasy-week")
public class FantasyWeekController {
    private final FantasyWeekService fantasyWeekService;
    public FantasyWeekController(FantasyWeekService fantasyWeekService) {
        this.fantasyWeekService = fantasyWeekService;
    }

    @GetMapping("{fantasyWeekId}")
    public ResponseEntity<FantasyWeek> getFantasyWeekById(@PathVariable long fantasyWeekId) throws Error404 {
        FantasyWeek week = fantasyWeekService.getFantasyWeekById(fantasyWeekId);
        return ResponseEntity.ok(week);
    }

    @GetMapping("")
    public ResponseEntity<FantasyWeek> getFantasyWeekByWeekNumber(@RequestParam long fantasyLeagueId, @RequestParam long fantasyTeamId,
                                                                  @RequestParam int weekNumber) {
        FantasyWeek fantasyWeeks = fantasyWeekService.getFantasyWeekByWeekNumber(fantasyLeagueId, fantasyTeamId, weekNumber);
        return ResponseEntity.ok(fantasyWeeks);
    }
    @GetMapping("{fantasyLeagueId}")
    public ResponseEntity<List<FantasyWeek>> getFantasyWeeksByLeagueId(@PathVariable long fantasyLeagueId) {
        List<FantasyWeek> fantasyWeeks = fantasyWeekService.getFantasyWeeksByLeagueId(fantasyLeagueId);
        return ResponseEntity.ok(fantasyWeeks);
    }

    @GetMapping("active")
    public ResponseEntity<FantasyWeek> getActiveFantasyWeek(@RequestParam long fantasyLeagueId, @RequestParam long fantasyTeamId) {
        FantasyWeek fantasyWeeks = fantasyWeekService.getActiveFantasyWeek(fantasyLeagueId, fantasyTeamId);
        return ResponseEntity.ok(fantasyWeeks);
    }

    @GetMapping("date")
    public ResponseEntity<FantasyWeek> getFantasyWeekByDate(@RequestParam long fantasyLeagueId, @RequestParam long fantasyTeamId,
                                                            @RequestParam LocalDate date) {
        FantasyWeek fantasyWeeks = fantasyWeekService.getFantasyWeekByDate(fantasyLeagueId, fantasyTeamId, date);
        return ResponseEntity.ok(fantasyWeeks);
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
