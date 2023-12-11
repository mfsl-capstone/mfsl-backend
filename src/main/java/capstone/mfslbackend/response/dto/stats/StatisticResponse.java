package capstone.mfslbackend.response.dto.stats;

import capstone.mfslbackend.response.dto.TeamResponse;
import capstone.mfslbackend.response.dto.GameResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StatisticResponse implements Serializable {
    private TeamResponse team;
    private GameResponse games;
    private Integer offsides;
    private ShotResponse shots;
    private GoalResponse goals;
    private PassResponse passes;
    private TackleResponse tackles;
    private DuelResponse duels;
    private DribbleResponse dribbles;
    private FoulResponse fouls;
    private CardResponse cards;
    private PenaltyResponse penalty;
}
