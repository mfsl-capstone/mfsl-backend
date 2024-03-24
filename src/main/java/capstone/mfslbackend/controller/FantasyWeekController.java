package capstone.mfslbackend.controller;


import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.FantasyWeek;
import capstone.mfslbackend.service.FantasyWeekService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
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

}
