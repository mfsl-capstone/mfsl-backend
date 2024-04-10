package capstone.mfslbackend.DTO;

import capstone.mfslbackend.model.FantasyWeek;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
public class FantasyWeeksDTO {
    private List<List<FantasyWeekForTableDTO>> weeks;
    public FantasyWeeksDTO from(List<FantasyWeek> weeks) {
        this.weeks = new ArrayList<>();
        List<FantasyWeek> weeksCopy = new ArrayList<>(weeks);
        weeksCopy.sort(Comparator.comparingInt(FantasyWeek::getWeekNumber));
        for (int i = weeksCopy.get(0).getWeekNumber(); i <= weeksCopy.get(weeksCopy.size() - 1).getWeekNumber(); i++) {
            int finalI = i;
            this.weeks.add(weeksCopy.stream()
                    .filter(week -> week.getWeekNumber() == finalI)
                    .map(week -> new FantasyWeekForTableDTO().from(week))
                    .toList());
        }
        return this;
    }
}
