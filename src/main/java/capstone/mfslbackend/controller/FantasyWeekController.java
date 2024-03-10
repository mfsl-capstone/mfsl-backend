package capstone.mfslbackend.controller;

import capstone.mfslbackend.error.Error400;
import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.FantasyWeek;
import capstone.mfslbackend.service.FantasyWeekService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("{week-number}")
    public ResponseEntity<List<FantasyWeek>> getFantasyWeekByWeekNumber(@PathVariable int weekNumber) {
        List<FantasyWeek> fantasyWeeks = fantasyWeekService.getFantasyWeekByWeekNumber(weekNumber);
        return ResponseEntity.ok(fantasyWeeks);
    }

    @PostMapping("")
    public ResponseEntity<FantasyWeek> createFantasyWeek(@RequestParam int fantasyTeamId, @RequestParam int weekNumber)
            throws Error404, Error400 {
        FantasyWeek fantasyWeek = fantasyWeekService.createFantasyWeek(fantasyTeamId, weekNumber);
        if (fantasyWeek == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fantasyWeek);
    }

}
